---
name: clj-boot
description: Batteries included Clojure builds based on Boot.
---

## Getting Started

This Getting Started guide covers the common case of using ```clj-boot ``` as-is.  Advanced developers may want to clone clj-boot and customize it for their own projects and use-cases.

## Minimal build.boot

The following ```build.boot``` is a good start for new projects.

Since ```clj-boot``` provides default build tasks maintained in a single place, and multiple build tasks consume the same metadata, ```clj-boot``` supplies the basic project metadata in a map, and uses that map to configure itself in ```set-task-options!``` (at the bottom of the file).

The resulting build file does not prevent you from composing the built-in tasks with your own or creating your own tasks, but I feel delivers the best of the declarative and task-based build worlds.

```clojure
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

## ```/resources/index.md``` is used to generate the site home page

Currently the template hard-codes a Twitter Bootstrap based site.  At some point I'll find a way to make that swappable/themeable.

For now, here's what you need to know:

* Metadata at the top of the Markdown file is merged into the site template.
* The master site template must be named ```index.md``` and must be stored in your ```resources``` folder.
* Codox is automatically placed in a ```codox``` folder inside the ```site``` folder, so be sure to link that somewhere in your template.
* ```.md``` files in your ```docs``` folder will automatically be added to your Codox.
* [Something like this](https://raw.githubusercontent.com/coconutpalm/clj-boot/master/resources/example-index.md) is a good site ```index.md``` start since it provides the latest Maven/Lein/Boot/Ivy coordinates and links to the generated Codox.


## If you want to deploy to Clojars...

Clojars deployment for ```:open-source``` projects is supported out of the box.  Here's how.

Prerequisites:

* A PGP public/private key pair
* A Clojars account

(I won't go into how to install PGP for each platform and generate a public/private key pair here; there are plenty of other good tutorials online.  See https://gist.github.com/chrisroos/1205934 under "method 2" to import deployment/signing keys into your account if somebody else is managing keys for your organization.)

Once you have these things, just set the appropriate environment variables and ```boot release``` will deploy to Clojars and push your documentation to a branch called ```gh-pages``` in your Git repo.  Placing a Bash script like the following (or the equivalent on Windows) in an appropriate place will do:

```bash
#!/usr/bin/env bash

export BOOT_JVM_OPTIONS='-client -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xverify:none -Xmx2g -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:-OmitStackTraceInFastThrow'

export CLOJARS_USER='clojars-username'
export CLOJARS_PASS='clojars-password'
export CLOJARS_GPG_USER='username@host.for.gpg.com'
export CLOJARS_GPG_PASS='gpg-key-password-for-signing'
```

## Next steps

The ```clj-boot``` [Codox](codox/index.html) lists the supplied Boot tasks along with hints for when to use each one.
