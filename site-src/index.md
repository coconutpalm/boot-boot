---
name: boot Boot
description: Batteries-included Clojure builds.
---
## Why boot Boot?  Here are a few reasons:

Clojure's Boot build tool provides incredible flexibility for composing simple build tasks like ```compile``` or ```translate-markdown``` into a complete development and release workflow.

The challenge is that integrating the more sophisticated Boot tasks requires a nontrivial amount of effort.  The ```boot Boot``` project performs those integrations so you don't have to and maintains them in a single place.  For example, ```boot Boot```:

* Provides all of the tools needed to edit (and automatically reload) your Clojure application live, as it is running, while you are developing it.
* Extends the live editing experience to writing project documentation using Markdown and Codox, styled using Twitter Bootstrap.  When you save a Markdown file, it is quickly translated into HTML and the web browser is automatically reloaded (using ```LiveReload.js```).
* Integrates all the tools to release ```:open-source``` projects to Clojars, update the project web site on Github Pages, tag the release, and push the release tag to Github.  (```:private``` projects are protected against accidentally executing this task.)

 ```boot Boot``` is built, documented, and deployed using itself.


## How?

Preconfigures Boot with the following common tasks/features:

* ```boot dev```
    * Automatically rebuilds, retests, and reloads the REPL whenever you save.
    * [Nightlight](https://sekao.net/nightlight/) web notebook / code editor with your latest code automatically (re)loaded.
* ```boot write-site``` - Create a beautiful project web site using Markdown, styled using Twitter Bootstrap.
    * While under development, your site is hosted at http://localhost:3000
    * Just write content in Markdown and put it in the ```site-src``` folder.
    * On save, automatically updates your site and reloads your web browser (using ```LiveReload.js```).
    * Fast!  Only tens of milliseconds between saving your Markdown file and seeing the result in your browser.
* ```boot write-full-site``` - Like ```boot write-site```, and generates full API documentation with Codox on each save too.
    * Useful when editing docstrings in Clojure code.
* ```boot release-site```
    * Quickly and automatically publishes your full project web site to Github Pages.
* ```boot release``` - Deploy projects of type ```:open-source``` to Clojars and release the latest project web site to Github Pages.
    * Just set your credentials in the right environment variables.
    * (See the [Getting Started](getting-started.html) guide for more.)

(...and more, of course.  See the [Codox](codox/index.html) for details.)

## What's new?

See the [New and Noteworthy](new-and-noteworthy.html) document for details on what features have been added to each release.

## Install / use

```boot Boot``` uses a standard Boot project directory structure, with some extra conventions about how to structure your ```build.boot``` file that reduces repetition.  Sample starter files are included in the [Getting Started](getting-started.html) guide.

The latest version is:

* [![Clojars Project](https://img.shields.io/clojars/v/coconutpalm/boot-boot.svg)](https://clojars.org/coconutpalm/boot-boot)

All Boot tasks are documented in the [Codox](codox/index.html).


## Roadmap

### 1.0

* Name and description metadata in web site should come from the build.boot file and not from the yaml header in the Markdown file.
* Library development template project in Github.
* Rework Getting Started Guide to reference template projects.
* Parameterize:
    * Doc template function
    * Documentation web server used during development port number.

### 1.5

* Web development template project (using Hoplon/Javelin/Castra) in Github.
* Allow to customize options passed to Hiccup.
* Introduce a project CSS file, making it easier to style/customize/rebrand Bootstrap elements in the web site.
* Syntax highlighting for code samples in the web site.

### 2.0

* ```boot new``` template for boot Boot projects
* Deploy somewhere other than Clojars for :private projects?
    * (Would need a way to register a deployer, since I can't anticipate all use-cases.)

### Uncommitted

* Use boot Boot itself to create new projects from template projects, or maybe from itself.
    * Pros: ```boot Boot``` can reuse the template files in its own documentation, easing maintenance.
    * Cons: Non-standard.
* Extend the web site use cases beyond simple documentation.
    * Site map?
    * Blog?
