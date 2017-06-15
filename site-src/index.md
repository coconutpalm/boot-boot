---
name: clj-boot
description: Batteries-included Clojure builds based on Boot.
---
## Why clj-boot?  Here are a few reasons:

* Boot provides all of the tools needed to edit (and minimally reload) your Clojure application live, as it is running.  ```clj-boot``` integrates those tools in one place so you don't have to.
* ```clj-boot``` extends the live editing experience to writing project documentation using Markdown and Codox, styled using Twitter Bootstrap.
* A single command releases ```:open-source``` projects to Clojars, updates the project web site on Github Pages, tags the release, and pushes the release tag to Github.  And ```:private``` projects are protected against accidentally executing this task.

clj-boot is built, documented, and deployed using itself.


## How?

Preconfigures Boot with the following common tasks/features:

* ```boot dev```
    * Automatically rebuilds, retests, and reloads the REPL whenever you save.
    * [Nightlight](https://sekao.net/nightlight/) web notebook / code editor with your latest code automatically (re)loaded.
* ```boot write-site``` - Create a beautiful project web site styled using Twitter Bootstrap using Markdown.
    * While under development, your site is hosted at http://localhost:3000
    * Just write content in Markdown and put it in the ```site-src``` folder.
    * On save, automatically updates your site and reloads your web browser (using ```LiveReload.js```).
    * Fast!  Only tens of milliseconds between saving your Markdown file and seeing the result in your browser.
* ```boot write-full-site``` - Like ```boot write-site```, but generates full API documentation with Codox on each save too.
    * Useful when editing docstrings in Clojure code for public consumption.
* ```boot release-site```
    * Quickly and automatically publishes your full project web site to Github Pages.
* ```boot release``` - Deploy projects of type ```:open-source``` to Clojars and release the latest project web site to Github Pages.
    * Just set your credentials in the right environment variables.
    * (See the [Getting Started](getting-started.html) guide for more.)

(...and more, of course.  See the [Codox](codox/index.html) for details.)


## Install / use

```clj-boot``` uses a standard Boot project directory structure, with some extra conventions about how to structure your ```build.boot``` file that reduces repetition.  Sample starter files are included in the [Getting Started](getting-started.html) guide.

The latest version is:

* [![Clojars Project](https://img.shields.io/clojars/v/coconutpalm/clj-boot.svg)](https://clojars.org/coconutpalm/clj-boot)

Out-of-the box Boot tasks are documented in the [Codox](codox/index.html).


## Roadmap

### 1.0

* Default Hiccup template to include a link to project home page.
* Name and description metadata in web site should come from the build.boot file and not from the yaml header in the Markdown file.
* Library development template project in Github.
* Rework Getting Started Guide to reference template projects.
* Parameterize the documentation web server used during development.
    * Port number

### 1.5

* Web development template project (using Hoplon/Javelin/Castra) in Github.
* Allow to customize options passed to Hiccup, including specifying a custom page template function.
* Introduce a project CSS file, making it easier to style/customize/rebrand Bootstrap elements in the web site.
* Syntax highlighting for code samples in the web site.

### 2.0

* ```boot new``` template for clj-boot projects
* Deploy somewhere other than Clojars for :private projects?
    * (Would need a way to register a deployer, since I can't anticipate all use-cases.)

### Uncommitted

* Use clj-boot itself to create new projects from template projects, or maybe from itself.
    * Pros: clj-boot can reuse the template files in its own documentation, easing maintenance.
    * Cons: Non-standard.
* Extend the web site use cases beyond simple documentation.
    * Site map?
    * Blog?
