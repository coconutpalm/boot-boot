(def task-options
  {:project  'coconutpalm/boot-boot
   :version  "0.6.3"
   :project-name "boot Boot"
   :project-openness :open-source

   :description "Batteries-included Clojure development and release workflows using Boot."
   :scm-url "https://github.com/coconutpalm/boot-boot"

   :test-sources nil
   :test-resources nil})


(set-env! :resource-paths #{"resources" "site-src"}
          :source-paths   #{"src" "test"}

          :dependencies '[[org.clojure/clojure        "1.9.0" :scope "provided"]
                          [metosin/spec-tools         "0.6.1"]
                          [coconutpalm/clj-foundation "0.10.0"]

                          ;; Boot tasks
                          [pandeiro/boot-http         "0.8.3"]
                          [deraen/boot-livereload     "0.2.1"]
                          [boot-codox                 "0.10.3"]
                          [perun                      "0.3.0"]
                          [cpmcdaniel/boot-copy       "1.0"]
                          [hiccup                     "1.0.5"]
                          [org.clojure/test.check     "0.9.0"]
                          [samestep/boot-refresh      "0.1.0"]
                          [nightlight                 "2.2.2"] ;; Nightlight 1.6.4?

                          [boot/new                   "0.5.2"]
                          [tolitius/boot-check        "0.1.9"]
                          [cloverage                  "1.0.10"]

                          [adzerk/bootlaces           "0.1.13"]
                          [adzerk/boot-test           "1.2.0"]
                          [adzerk/boot-jar2bin        "1.1.1"]
                          [boot/pod                   "2.7.2"] ;; FIXME: These need to be fixed to boot.version
                          [boot/aether                "2.7.2"]
                          [boot/worker                "2.7.2"]])


(require '[samestep.boot-refresh :refer [refresh]])
(require '[adzerk.boot-test :refer [test]])
(require '[adzerk.bootlaces :refer :all])
(require '[tolitius.boot-check :as check])
(require '[codox.boot :refer [codox]])
(require '[adzerk.boot-jar2bin :refer :all])
(require '[io.perun :refer :all])
(require '[pandeiro.boot-http :refer :all])
(require '[deraen.boot-livereload :refer [livereload]])
(require '[clj-boot.core :refer :all])


(set-task-options! task-options)
