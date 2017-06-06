(ns clj-boot.core
  "clj-boot's built-in tasks are defined here."
  (:refer-clojure :exclude [test reader-conditional tagged-literal])
  (:require [clojure.pprint :refer [pprint]]
            [boot.core :refer :all]
            [boot.util :refer :all]
            [boot.task.built-in :refer :all]
            [clj-boot.docs :as docs]
            [clj-boot.boot-cloverage :refer [cloverage]]
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


(def project-types
  "Available project types set.  Currently one of :open-source or :private.  Only :open-source projects
  can push to Clojars."
  #{:open-source :private})

(def project-type
  "The current project type ref.  Defaults to :private."
  (ref :private))


(deftask assert-project-type
  "A task that fails the build if the current project is not a legal type.  Legal types are members of the
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


(deftask generate-site
  "Generate web site from resources/index.md and Codox generated from source code.  See the Getting Started
Guide for details."
  []
  (comp (markdown)
     (render :renderer 'clj-boot.docs/renderer)
     (codox)
     (target)
     (copy :output-dir "./" :matching #{#"\.*site\.*"})))


(deftask write-site
  "Interactively work on web site documentation.  Watches the file system and calls (generate-site)
whenever anything changes."
  []
  (comp (watch)
     (generate-site)))


(deftask dev
  "Interactively dev/test with a live application that automatically reloads changed namespaces.
When you start dev mode, the nrepl server port is printed.  Also, automatically starts the 'nightlight'
web based Clojure notebook and live coding environment (prints the URL at startup)."
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
  "Build a jar and release it to the local repository."
  []
  (comp (test)
     (notify :audible true :visual true)
     (build-jar)
     (target)))


(deftask cmd
  "Run a shell command in a task."
  [r run COMMAND str "The shell command to run."]
  (let [args (delimited-words run)]
    (with-pre-wrap fileset
      (pprint `(apply dosh ~args))
      (apply dosh args)
      fileset)))


(deftask release-site
  "Push updated documentation to gh-pages.  See https://gist.github.com/cobyism/4730490
  for the technique used."
  [v version VERSION str "The current project version"]
  (comp (generate-site)
     (cmd :run "git add site")
     (cmd :run "git stage site")
     (cmd :run (str "git commit -a -m 'Added documentation for version " version "'"))
     (cmd :run "git subtree push --prefix site origin gh-pages")))


(deftask snapshot
  "Build and release a snapshot to Clojars.  Project type must be :open-source.

Depends on CLOJARS_USER, CLOJARS_PASS, CLOJARS_GPG_USER, CLOJARS_GPG_PASS, envars.
See the Getting Started Guide for details."
  []
  (comp (assert-project-type :expect :open-source)
     (release-local)
     (push-snapshot)))


(deftask release
  "Release a Jar to Clojars.  Project type must be :open-source.

Depends on CLOJARS_USER, CLOJARS_PASS, CLOJARS_GPG_USER, CLOJARS_GPG_PASS, envars.
See the Getting Started Guide for details."
  []
  (comp (assert-project-type :expect :open-source)
     (release-local)
     (release-site)
     (push-release)
     (cmd :run "git push origin --tags")))


(deftask uberjar
  "Run tests, and build an uberjar."
  []
  (comp (test)
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
  "Set default options for standard tasks.  This must be called at the end of build.boot.
  See the Getting Started Guide for details."
  [{:keys [project project-name project-openness description version scm-url test-sources test-resources push-repository]}]

  (bootlaces! version)
  (dosync (ref-set project-type project-openness))

  (set-env! :repositories #(conj % ["clojars-push" {:url "https://clojars.org/repo/"
                                                    :username (System/getenv "CLOJARS_USER")
                                                    :password (System/getenv "CLOJARS_PASS")}]))

  (task-options!
   release-site {:version version}

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
