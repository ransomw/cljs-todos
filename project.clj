(defproject todos "0.0.0-SNAPSHOT"

  :source-paths ["src/clj"]

  :dependencies [
                 ;; app libs
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [secretary "1.2.3"]
                 [org.omcljs/om "0.9.0"]
                 [com.andrewmcveigh/cljs-time "0.3.14"]
                 ;; dev libs
                 [figwheel "0.4.1"]
                 [figwheel-sidecar "0.4.1"]
                 [com.cemerick/piggieback "0.1.5"]
                 [weasel "0.7.0" :exclusions [org.clojure/clojurescript]]
                 ]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.2.5"]
            [cider/cider-nrepl "0.10.0-SNAPSHOT"]]

  :min-lein-version "2.5.3"

  ;; :repl-options {:init-ns todos.server
  ;;                :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :figwheel {:nrepl-port 7888
             :css-dirs ["hoodie_app/www/css"]}

  :cljsbuild {:builds
              {:app
               {:source-paths ["src/cljs"]
                :figwheel {:on-jsload "todos.core/main"}
                :compiler
                {
                 :main "todos.core"
                 :asset-path "js/cljs_out/out"
                 :output-to "hoodie_app/www/js/cljs_out/app.js"
                 :output-dir "hoodie_app/www/js/cljs_out/out"
                 :source-map "hoodie_app/www/js/cljs_out/out.js.map"
                 :optimizations :none
                 :source-map-timestamp true
                 }}}}

  )
