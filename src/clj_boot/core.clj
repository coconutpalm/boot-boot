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

            [pandeiro.boot-http :refer :all]
            [cpmcdaniel.boot-copy :refer [copy]]
            [codox.boot :refer [codox]]
            [io.perun :refer :all]
            [deraen.boot-livereload :refer [livereload]]
            [samestep.boot-refresh :refer [refresh]]

            [adzerk.boot-test :refer [test]]
            [adzerk.bootlaces :refer :all]
            [tolitius.boot-check :as check]
            [adzerk.boot-jar2bin :refer :all]))


(def project-types
  "Available project types set.  Currently one of :open-source or :private.  Only :open-source projects
  can push to Clojars or gh-pages."
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


;; Future
#_(deftask metadata
   "Merge kvs in map into global markdown translator metadata as well as each file's metadata."
   [v values META edn]
   (with-pre-wrap fileset
     (let [file-meta (perun/get-meta fileset)
           global-meta (perun/get-global-meta fileset)
           new-file-meta (reduce (fn [file-meta [k v]] (map #(assoc % k v) file-meta)) file-meta values)
           new-global-meta (reduce (fn [global-meta [k v]] (assoc global-meta k v)) global-meta values)
           updated-files   (perun/set-meta fileset new-file-meta)]
       (perun/set-global-meta updated-files new-global-meta))))



(deftask generate-site
  "Generate web site from site-src/*.md only.  This version is much faster if you are just working
on the Markdown documentation.  See the Getting Started Guide for details."
  [r renderer RENDERER sym "A renderer function per Perun's documentation.  Defaults to 'clj-boot.docs/renderer."]
  (comp (markdown)
     (render :renderer (or renderer 'clj-boot.docs/renderer))))


(deftask generate-full-site
  "Generate web site from site-src/*.md and Codox generated from source code.  See the Getting Started
Guide for details."
  [r renderer RENDERER sym "A renderer function per Perun's documentation.  Defaults to 'clj-boot.docs/renderer."]
  (comp (generate-site :renderer renderer)
     (codox)))


(deftask serve-site
  "Serve the current web site documentation at localhost:3000.  Normally invoked composed with watchers and
generators.  e.g.: (boot (watch) (generate-site) (serve-site))"
  [p port PORT int "The port on localhost for serving the project web site.  Defaults to 3000."]
  (let [serve-args (concat [:resource-root "site"]
                           (if port
                             [:port port]
                             []))]
    (comp (livereload :snippet true :asset-path "site" :filter #"\.(css|html|js)$")
       (apply serve serve-args))))


(deftask write-site
  "Interactively work on web site documentation.  Watches the file system and calls (generate-site)
whenever anything changes."
  [r renderer RENDERER sym "A renderer function per Perun's documentation.  Defaults to 'clj-boot.docs/renderer."
   p port PORT int "The port on localhost for serving the project web site.  Defaults to 3000."]
  (comp (watch)
     (generate-site :renderer renderer)
     (serve-site :port port)))


(deftask write-full-site
  "Interactively work on web site documentation.  Watches the file system and calls (generate-full-site)
whenever anything changes."
  [r renderer RENDERER sym "A renderer function per Perun's documentation.  Defaults to 'clj-boot.docs/renderer."
   p port PORT int "The port on localhost for serving the project web site.  Defaults to 3000."]
  (comp (watch)
     (generate-full-site :renderer renderer)
     (serve-site :port port)))


(deftask cider
  "Add Cider dependencies for development tools that expect Cider middleware in the REPL.  Not all tools
expect this and it adds dependencies to your build so it's not enabled by default.  Usage example:

\"boot cider dev\"  ; or
(boot cider dev)"
  []
  (require 'boot.repl)
  (swap! @(resolve 'boot.repl/*default-dependencies*)
         concat '[[nrepl "0.6.0"]
                  [cider/cider-nrepl "0.21.1"]
                  [refactor-nrepl "2.4.0"]])
  (swap! @(resolve 'boot.repl/*default-middleware*)
         concat '[cider.nrepl/cider-middleware
                  refactor-nrepl.middleware/wrap-refactor])
  identity)


(deftask dev
  "Interactively dev/test with a live application that automatically reloads changed namespaces.
When you start dev mode, the nrepl server port is printed.  To use this with Cider middleware,
add the \"cider\" task ahead of the \"dev\" task.  For example:

\"boot cider dev \"  ; or
(boot cider dev)"
  []
  (comp (watch)
     (refresh)
     (repl :server true)
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
  [v version VERSION str "The current project version"
   r renderer RENDERER sym "A renderer function per Perun's documentation.  Defaults to 'clj-boot.docs/renderer."]
  (comp (assert-project-type :expect :open-source)
     (generate-full-site :renderer renderer)
     (target)
     (copy :output-dir "./" :matching #{#"\.*site\.*"})
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
  [r renderer RENDERER sym "A renderer function per Perun's documentation.  Defaults to 'clj-boot.docs/renderer."]
  (comp (assert-project-type :expect :open-source)
     (release-local)
     (release-site :renderer renderer)
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
  (comp (aot :all true)
     (uberjar)
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
