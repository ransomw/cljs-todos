(ns todos.components.nav
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.routes :as rts]
   ))


(defn nav-view [data owner]
  (om/component
   (let [username (:username data)]
     (dom/div
      nil
      (dom/div
       #js {:className "brand"}
       (dom/a #js {:href "#/"} "todos"))
      (if username
        (dom/ul
         #js {:className "locations"}
         (domh/li-link (rts/new-todo-path) "new todo")
         (domh/li-link (rts/all-todos-path) "all todos")
         ))
      (dom/ul
       #js {:className "actions"}
       (if username
         (domh/li-link (rts/logout-path) "Logout" :a-class "button")
         (domh/li-link (rts/login-path) "Login" :a-class "button")
         ))
      )
     )))
