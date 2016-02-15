# Clojurescript todos

### description

hierarchical todos in `.cljs` with due-dates and priorities.

### usage

##### environment
The development and deploy environments expect
[clojurescript](https://github.com/clojure/clojurescript/wiki/Quick-Start)
and
[leiningen](http://leiningen.org/)
as well as
[node](https://github.com/creationix/nvm)
`v5.5.0` and a global install of
[bower](http://bower.io/).

##### build and run

The following steps ought to get a demo up and running.

* build the javascript client by running `lein cljsbuild once` in the
   project root directory
* start the [hoodie](http://hood.ie/) server from the `hoodie_app`
   directory with
   > `npm install`
   > `bower install`
   > `node bin/run`
   and check the terminal output for the `WWW` port
   to point your browser to after setup
* cache the hoodie client on the filesystem for faster page-loads
  by running `./curl_hoodie.sh $PORT` in the `hoodie_app` directory,
  where `$PORT` is the hoodie `WWW` port.

##### develop

the development setup uses
[figwheel](https://github.com/bhauman/lein-figwheel#features)
for code reloading and an interactive REPL.
run `lein figwheel` in a seperate terminal and reload the page to get started!
