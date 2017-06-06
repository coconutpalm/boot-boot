---
name: clj-boot
description: Batteries included Clojure builds based on Boot.
---
## Benefits

* A clean, DRY format for common Boot metadata without sacrificing Boot's flexibility.
* Maintain common build tasks in a single place, reducing repetition in your build files.  (Either clone this repository and customize it for your site or use it as-is from Clojars.)
* ```boot dev```
    * Automatically rebuilds, retests, and reloads the REPL whenever you save.
    * [Nightlight](https://sekao.net/nightlight/) web notebook with your latest code automatically (re)loaded.
* ```boot write-site``` - Easily create a project web site, styled with Twitter Bootstrap.
    * Just write content in Markdown.  The site is recreated on save.
    * Automatically generates API documentation using Codox.
    * Instantly publish to Github Pages with ```boot release-site```.
* ```boot release``` - Deploy projects of type ```:open-source``` to Clojars.  Just set your credentials in the right environment variables.

clj-boot is built, documented, and deployed using itself.


## Install / use

```clj-boot``` uses a standard Boot project directory structure, but adds some conventions about how to structure your ```build.boot``` file.  Sample starter files are included in the [Getting Started](getting-started.html) guide.

The latest version is:

* [![Clojars Project](https://img.shields.io/clojars/v/coconutpalm/clj-boot.svg)](https://clojars.org/coconutpalm/clj-boot)

## Documentation

* [Getting Started](getting-started.html)
* [Codox](codox/index.html)


## Roadmap

* ```boot new``` template for clj-boot projects
* Themes for documentation pages?
* Move index.md out of ```resources``` if possible.
* Generate OSGi metadata / support OSGi runtime
* Deploy somewhere other than Clojars for :private projects?
