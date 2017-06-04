---
name: clj-boot
description: Batteries included Clojure builds based on Boot.
---

## Getting Started

This Getting Started guide covers the common case of using ```clj-boot ``` as-is.  A future version will supply a Boot/new template to automate some of this setup.

## Minimal build.boot

``` Clojure
(def task-options
  {:project 'you/your-project
   :version "0.1.0-SNAPSHOT"
   :project-name "your-project"
   :project-openness :open-source  ; (or :closed-source)

   :description "Project description."

   :scm-url "https://github.com/coconutpalm/clj-foundation"})


(set-env! :resource-paths #{"resources"}
          :source-paths   #{"src" "test"}

          :dependencies   '[[org.clojure/clojure  "1.8.0"]  ; Your Clojure version

                            ; Your dependencies here

                            [coconutpalm/clj-boot "LATEST" :scope "test"]]) ; Or a specific version

(require '[clj-boot.core :refer :all])

(set-task-options! task-options)
```

## /resources/index.md is used to generate the site home page

Currently the template hard-codes a Twitter Bootstrap based site.  At some point I'll find a way to make that swappable.  Codox is automatically placed in a ```codox``` folder inside the ```site``` folder, so be sure to link that somewhere in your template.

Metadata at the top is merged into the site template.

Something like the following is a good start:


## If you want to deploy to Clojars...

Prerequisites:
* A PGP public/private key pair
* A Clojars account

(I won't go into how to install PGP for each platform and generate a public/private key pair here; there are plenty of other good tutorials online.)

Once you have these things, just set the appropriate environment variables and ```boot release``` will deploy to Clojars and push your documentation to a branch called ```gh-pages``` in your Git repo.
