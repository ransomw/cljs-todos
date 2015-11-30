(ns todos.components-main
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.state :as st]
   [todos.routes :as rts]
   )
  (:use
   [todos.components.nav :only [nav-view]]
   [todos.components.signup :only [signup-view]]
   [todos.components.login :only [login-view]]
   [todos.components.re-login :only [re-login-view]]
   [todos.components.home :only [home-view]]
   [todos.components.new-todo :only [new-todo-view]]
   [todos.components.view-todo :only [view-todo-view]]
   [todos.components.all-todos :only [all-todos-view]]
   [todos.components.config :only [config-view]]
   ))


(defn unknown-route-view [data owner]
  (reify
      om/IRender
    (render [this]
      (dom/h1 nil "Unknown route")
      )))

(defn get-curr-main-view [route-path logged-in?]
  (cond
    (= route-path (rts/home-path))
    (if logged-in?
      home-view
      signup-view)
    (= route-path (rts/login-path))
    login-view
    (= route-path (rts/new-todo-path))
    new-todo-view
    (= route-path (rts/all-todos-path))
    all-todos-view
    (= route-path (rts/config-path))
    config-view
    (= route-path (rts/view-todo-path))
    view-todo-view
    (= route-path (rts/re-login-path))
    re-login-view
    :else
    unknown-route-view
    ))

(defn main-view [data owner]
  (reify
      om/IRender
    (render [this]
      (let [route-path (:path (:route data))
            curr-view (get-curr-main-view route-path (:username data))
            view-params
            (assoc
             (dissoc data :route)
             :route-params (:params (:route data)))]
        (om/build* curr-view view-params)))))

(defn load []
  (om/root
   nav-view st/app-state
   {:target (. js/document (getElementById "nav-main"))})
  (om/root
   main-view st/app-state
   {:target (. js/document (getElementById "app-main"))})
  )
