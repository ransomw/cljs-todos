(ns todos.server
  (:require
   [cemerick.piggieback :as piggieback]
   [weasel.repl.websocket :as weasel]
   [figwheel-sidecar.auto-builder :as fig-auto]
   [figwheel-sidecar.core :as fig]
   ))

(defn browser-repl []
  (let [repl-env (weasel/repl-env :ip "0.0.0.0" :port 9001)]
    (piggieback/cljs-repl :repl-env repl-env)))

(defn start-figwheel []
  (let [server (fig/start-server {})
        ;; should match project.clj
        config {:builds
                [{:id "dev"
                  :source-paths ["src/cljs"]
                  :compiler {
                             :main "todos.core"
                             :asset-path "js/cljs_out/out"
                             :output-to "hoodie_app/www/js/cljs_out/app.js"
                             :output-dir "hoodie_app/www/js/cljs_out/out"
                             :source-map "hoodie_app/www/js/cljs_out/out.js.map"
                             :optimizations :none
                             :source-map-timestamp true
                             ;; :source-map true
                             }}]
                :figwheel-server server}]
    (fig-auto/autobuild* config)))
