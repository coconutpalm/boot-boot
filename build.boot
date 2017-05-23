(def task-options
  {:project  'coconutpalm/clj-boot
   :version  "0.3.3"
   :project-name "clj-boot"
   :project-openness :open-source

   :description "A standard project configuration based on Boot making JAR projects easier to create and maintain."
   :scm-url "https://github.com/coconutpalm/clj-boot"

   :test-sources nil
   :test-resources nil})


(set-env! :resource-paths #{"resources"}
          :source-paths   #{"src" "test"}

          :dependencies '[[org.clojure/clojure        "1.8.0"]
                          [clojure-future-spec        "1.9.0-alpha16-1"]
                          [metosin/spec-tools         "0.1.1"]
                          [org.clojure/test.check     "0.9.0"]
                          [coconutpalm/clj-foundation "0.10.0"]

                          ;; Boot tasks
                          [boot-codox                 "0.10.2"]
                          [perun                      "0.3.0"]
                          [hiccup                     "1.0.5"]
                          [org.clojure/test.check     "0.9.0"]
                          [samestep/boot-refresh      "0.1.0"]
                          [nightlight                 "LATEST"]

                          [boot/new                   "0.5.1"]
                          [tolitius/boot-check        "0.1.4"]
                          [cloverage                  "1.0.9"]

                          [adzerk/bootlaces           "0.1.13"]
                          [adzerk/boot-test           "1.2.0"]
                          [adzerk/boot-jar2bin        "1.1.0"]
                          [boot/pod                   "2.7.1"] ;; FIXME: These need to be fixed to boot.version
                          [boot/aether                "2.7.1"]
                          [boot/worker                "2.7.1"]])


(require '[samestep.boot-refresh :refer [refresh]])
(require '[adzerk.boot-test :refer [test]])
(require '[adzerk.bootlaces :refer :all])
(require '[tolitius.boot-check :as check])
(require '[codox.boot :refer [codox]])
(require '[adzerk.boot-jar2bin :refer :all])
(require '[io.perun :refer :all])
(require '[clj-boot.core :refer :all])


(set-task-options! task-options)
