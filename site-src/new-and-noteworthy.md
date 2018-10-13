---
name: boot Boot
description: Batteries-included Clojure development and release workflows using Boot.
---
## New and Noteworthy

Generally I follow a policy of releasing features whenever they are ready, not when a new major release is ready.  This page records the features that have gone into each release.

### 0.6.4

* The `uberbin` task wasn't `:aot :all`ing code.  Fixed.
* Added optional Cider support via a (cider) task for dev environments that expect Cider middleware in the REPL. Enable by placing ahead of the repl. e.g.: `$ boot cider dev`

### 0.6.1

* Upgrade to Clojure 1.9 and upgrade other dependencies

### 0.6

* The Hiccup template used for documentation can be overridden as a parameter to the documentation tasks.
* The port number used by the documentation web server can be overridden as a parameter to the write-site and write-full-site tasks.


### 0.5.3

* Renamed clj-boot to boot-boot because Boot itself is normally named boot-clj, leading to possible confusion.


### 0.5.2

* Documentation template automatically links back to the home page.


### 0.5

* Write documentation in Markdown; the browser is automatically reloaded on save.
* Generate Codox API documentation into a subdirectory of the documentation folder to make it easy to link from the documentation.
* ```boot release``` now releases everything in one step:
    * Code to clojars
    * Documentation to gh_pages
    * Release tags to Github.
* General code cleanup.
