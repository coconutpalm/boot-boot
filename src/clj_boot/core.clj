(ns clj-boot.core
  (:refer-clojure :exclude [test reader-conditional tagged-literal])
  (:require [clojure.pprint :refer [pprint]]
            [boot.core :refer :all]
            [boot.util :refer :all]
            [boot.task.built-in :refer :all]
            [clj-boot.docs :as docs]
            [clj-boot.boot-cloverage :rever [cloverage]]
            [clj-boot.string :refer [delimited-words]]

            [cpmcdaniel.boot-copy :refer [copy]]
            [codox.boot :refer [codox]]
            [io.perun :refer [markdown render]]
            [samestep.boot-refresh :refer [refresh]]
            [nightlight.boot :refer [nightlight]]

            [adzerk.boot-test :refer [test]]
            [adzerk.bootlaces :refer :all]
            [tolitius.boot-check :as check]
            [adzerk.boot-jar2bin :refer :all]))


(def project-types #{:open-source :private})

(def project-type (ref :private))


(deftask assert-project-type
  "Fails the build if the current project is not a legal type.  Legal types are members of the
project-types set.  In addition, may test that the current project is exactly a single type via
the 'expect' parameter."
  [e expect PROJECT-TYPE kw "The expected project type"]
  (fn middleware [next-handler]
    (fn handler [fileset]
      (if-not (project-types @project-type)
        (throw (ex-info (str "This project is " @project-type " but must be one of " project-types)
                        {})))
      (if-not (= expect @project-type)
        (throw (ex-info (str "This project is " @project-type " but must be " expect
                             ".\nto perform this operation.\n  Supported project types: " project-types)
                        {})))
      (next-handler fileset))))


(deftask test-with-settings
  "Run (test) with the specified settings added to the environment.  Restores the original environment
  after running tests."
  [s sources PATH str "The directory where test source code is located."
   r resources PATH str "The directory where testing resources are located."]
  (let [test-middleware (test)
        test-handler (test-middleware identity)]
    (fn middleware [next-handler]
      (fn handler [fileset]
        (let [baseline-sources (get-env :source-paths)
              baseline-resources (get-env :resource-paths)]

          (when sources
            (set-env! :source-paths #(conj % sources)))
          (when resources
            (set-env! :resource-paths #(conj % resources)))

          (let [fileset' (test-handler fileset)]
            (set-env! :source-paths baseline-sources
                      :resource-paths baseline-resources)
            (next-handler fileset')))))))


(deftask generate-site
  "Generate updated site."
  []
  (comp (markdown)
     (render :renderer 'clj-boot.docs/renderer)
     (codox)
     (target)
     (copy :output-dir "./" :matching #{#"\.*site\.*"})))


(deftask write-site
  "A development mode for interactively working on the web site documentation."
  []
  (comp (watch)
     (generate-site)))


(deftask dev
  "Interactively dev/test"
  []
  (comp (watch)
     (refresh)
     (repl :server true)
     (nightlight :port 0)
     (test)
     (notify :audible true :visual true)))


(deftask lint
  "Reveal uncleanliness in the codebase."
  []
  (comp (check/with-yagni)
     (check/with-eastwood)
     (check/with-kibit)
     (check/with-bikeshed)))


(deftask release-local
  "Build a jar and release it to the local repo."
  []
  (comp (test)
     (notify :audible true :visual true)
     (build-jar)
     (target)))


(deftask cmd
  "Run a shell command"
  [r run COMMAND str "The shell command to run."]
  (let [args (delimited-words run)]
    (with-pre-wrap fileset
      (pprint `(apply dosh ~args))
      (apply dosh args)
      fileset)))


(deftask release-site
  "Push updated documentation to gh-pages.  See https://gist.github.com/cobyism/4730490
  generate-site must be called first to update the web site."
  [v version VERSION str "The current project version"]
  (comp
   (cmd :run (str "git add site"))
   (cmd :run (str "git stage site"))
   (cmd :run (str "git commit -a -m 'Added documentation for version " version "'"))
   (cmd :run "git subtree push --prefix site origin gh-pages")))


(deftask snapshot
  "Build and release a snapshot."
  []
  (comp (assert-project-type :expect :open-source)
     (release-local)
     (push-snapshot)))


(deftask release
  "Release a Jar.  If project-type is :open-source, pushes to Clojars.  If project-type is :private,
pushes to a configured repository.  If project-type is :private and no respository is configured,
aborts.

For Clojars, depends on CLOJARS_USER, CLOJARS_PASS, CLOJARS_GPG_USER, CLOJARS_GPG_PASS, envars."
  []
  (comp (assert-project-type :expect :open-source)
     (release-local)
     (push-release)))


(deftask uberjar
  "Run tests, and build an uberjar."
  []
  (comp (test-with-settings)
     (notify :audible true :visual true)
     (pom)
     (uber)
     (jar)
     (target)))


(deftask uberbin
  "Run tests, and build a direct-executable, aot'd uberjar."
  []
  (comp (uberjar)
     (bin)))


(defn set-task-options!
  "Set default options for standard tasks."
  [{:keys [project project-name project-openness description version scm-url test-sources test-resources push-repository]}]

  (bootlaces! version)
  (dosync (ref-set project-type project-openness))

  (set-env! :repositories #(conj % ["clojars-push" {:url "https://clojars.org/repo/"
                                                    :username (System/getenv "CLOJARS_USER")
                                                    :password (System/getenv "CLOJARS_PASS")}]))

  (task-options!
   release-site {:version version}

   test-with-settings {:sources test-sources
                       :resources test-resources}

   push (cond
          push-repository                   push-repository
          (= :open-source project-openness) {:repo "deploy-clojars"
                                             :gpg-sign true
                                             :ensure-release true
                                             :gpg-user-id (System/getenv "CLOJARS_GPG_USER")
                                             :gpg-passphrase (System/getenv "CLOJARS_GPG_PASS")}
          :else                             {})

   pom {:project     project
        :description description
        :version     version
        :scm         {:url scm-url}}

   render {:out-dir "site"}

   codox {:name project-name
          :description description
          :version     version
          :metadata    {:doc/format :markdown}
          :output-path (str "site/codox/")
          :source-uri  (str scm-url "/blob/{version}/{filepath}#L{line}")}))
