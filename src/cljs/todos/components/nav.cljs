(ns todos.components.nav
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.routes :as rts]
   ))


(defn nav-view [data owner]
  ;; todo: this let binding is probably why the reload after signup
  ;;       bug occurs
  (let [username (:username data)]
    (om/component
     (dom/div
      nil
      (dom/div
       #js {:className "brand"}
       (dom/a #js {:href "#/"} "todos"))
      (if username
        (dom/ul
         #js {:className "locations"}
         (domh/li-link (rts/new-todo-path) "new todo")
         ))
      (dom/ul
       #js {:className "actions"}
       (if username
         (domh/li-link (rts/logout-path) "Logout" :a-class "button")
         (domh/li-link (rts/login-path) "Login" :a-class "button")
         ))
      )
     )))
