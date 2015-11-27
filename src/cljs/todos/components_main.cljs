(ns todos.components-main
  (:require
   [clojure.string :as str]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.component-helpers :as comh]
   [todos.state :as st]
   [todos.routes :as rts]
   [todos.util :as util]
   )
  (:use
   [todos.components.nav :only [nav-view]]
   [todos.components.signup :only [signup-view]]
   [todos.components.login :only [login-view]]
   [todos.components.re-login :only [re-login-view]]
   [todos.components.todo-list :only [make-todo-list-view]]
   [todos.components.home :only [home-view]]
   [todos.components.new-todo :only [new-todo-view]]
   [todos.components.view-todo :only [view-todo-view]]
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
    (= route-path (rts/view-todo-path))
    view-todo-view
    (= route-path (rts/re-login-path))
    re-login-view
    :else
    unknown-route-view
    ))

;; note this is currently ignoring route params
(defn get-main-view-state [data]
  (let [route-path (:path (:route data))
        curr-view (get-curr-main-view route-path (:username data))]
    {:path route-path
     :view (om/build* curr-view (dissoc data :route))}))

(defn main-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      (get-main-view-state data))
    om/IWillReceiveProps
    (will-receive-props [this next-props]

      (println "main view recv props")

      (if (not (= (:path (om/get-render-state owner))
                  (:path (:route next-props))))
        (om/set-state! owner (get-main-view-state next-props))))

    ;; om/IWillUpdate
    ;; (will-update [this next-props next-state]

    ;;   (println "main view update")

    ;;   ;; (println next-state)

    ;;   )

    om/IRenderState
    (render-state [this {:keys [path view]}]

      (println "main view render state")

      ;; (let [route-path (:path (:route data))
      ;;       curr-view (get-curr-main-view route-path (:username data))]
      ;;   (om/build curr-view (dissoc data :route)
      ;;             {:state (om/get-state owner)}))


      ;; (:view (get-main-view-state data))

      (dom/div nil view)

      )))

(defn load []
  (om/root
   nav-view st/app-state
   {:target (. js/document (getElementById "nav-main"))})
  (om/root
   main-view st/app-state
   {:target (. js/document (getElementById "app-main"))})
  )
