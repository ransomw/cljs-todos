(ns todos.core
  (:require
   [todos.routes :as routes]
   [todos.components-main :as comp]
   [todos.state :as st]
   [figwheel.client :as figwheel :include-macros true]
   [cljs.core.async :refer [put!]]
   [weasel.repl :as weasel]
   ))

(enable-console-print!)

(defn main []
  (println "Hiya!!!")
  (st/init-state)
  (comp/load)
  )

;; (figwheel/watch-and-reload
;;   :websocket-url "ws://localhost:3449/figwheel-ws"
;;   :jsload-callback (fn []
;;                      (main)))


;; (when-not (weasel/alive?)
;;   (weasel/connect
;;    "ws://localhost:9001" :verbose true :print #{:repl :console}))

(main)
(routes/start-routing)
