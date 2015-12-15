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

(main)
(routes/start-routing)
