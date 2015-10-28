(ns todos.components
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   [todos.routes :as rts]
   ))

(defn nav-view [data owner]
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
         (domh/li-link (rts/logout-path) "Logout" {:a "button"})
         (domh/li-link (rts/login-path) "Login" {:a "button"})
         ))
      )
     )))

(defn load-nav []
  (om/root nav-view st/app-state
           {:target (. js/document (getElementById "nav-main"))})
  )

(defn signup [data owner]
  (let [
        username (.-value (om/get-node owner "username"))
        password (.-value (om/get-node owner "password"))
        pass-confirm (.-value (om/get-node owner "pass-confirm"))
        ]
    (if (= password pass-confirm)
      (-> js/hoodie.account
          (.signUp username password)
          (.done (fn [new-username]
                   (js/alert "signed up!")))
          (.fail (fn [err]
                   (println "signup error")
                   (js/console.log err)
                   (js/alert "sign up failed"))))
      (js/alert "passwords don't match")
    )
  ))

(defn signup-view [data owner]
  (reify
      om/IRender
    (render [_]
      (domh/center-div
       (dom/h3 nil "Sign up")
       (domh/input "Username:" "text" "username")
       (domh/input "Password:" "password" "password")
       (domh/input "Repeat password:" "password" "pass-confirm")
       (dom/button
        #js {:onClick #(signup data owner)} "Signup!")
       )
      )))

(defn login [data owner]
  (let [
        username (.-value (om/get-node owner "username"))
        password (.-value (om/get-node owner "password"))
        ]
    (-> js/hoodie.account
        (.signIn username password #js {:moveData true})
        (.done (fn [login-username]
                 (rts/navigate-to (rts/home-path))))
        (.fail (fn [err]
                 (println "signin error")
                 (js/console.log err)
                 (js/alert "login failed")))
    )
  ))

(defn login-view [data owner]
  (reify
      om/IRender
    (render [this]
      (domh/center-div
       (dom/h3 nil "Login")
       (domh/input "Username" "text" "username")
       (domh/input "Password" "password" "password")
       (dom/button
        #js {:onClick #(login data owner)} "Login")
       )
      )))

(defn home-view [data owner]
  (reify
      om/IRender
    (render [this]
      (dom/p nil (:mstr data))
      )))

(defn new-todo [data owner]
  (let [
        title (.-value (om/get-node owner "title"))
        date (.-value (om/get-node owner "date"))
        ]
    (-> js/hoodie.store
        (.add
         (:todo st/store-types)
         {:title title
          :date date})
        (.done (fn [todo]
                 (rts/navigate-to (rts/home-path))))
        (.fail (fn [err]
                 (println "error adding todo")
                 (js/console.log err)
                 (js/alert "error adding todo")))
    )
  ))


(defn new-todo-view [data owner]
  (reify
      om/IRender
    (render [this]
      (domh/center-div
       (dom/h3 nil "New todo")
       (domh/input "Todo" "text" "title")
       (domh/input "Due" "date" "date")
       (dom/button
        #js {:onClick #(new-todo data owner)} "Add")
       )
      )))

(defn unknown-route-view [data owner]
  (reify
      om/IRender
    (render [this]
      (dom/h1 nil "Unknown route")
      )))

(defn main-view [data owner]
  (let [route-path (:path (:route data))]
    (println "main view got route")
    (println route-path)
    (cond
      (= route-path (rts/home-path))
        (if (:username data)
          (home-view data owner)
          (signup-view data owner))
      (= route-path (rts/login-path))
        (login-view data owner)
      (= route-path (rts/new-todo-path))
        (new-todo-view data owner)
      :else
        (unknown-route-view data owner)
        )
    )
  )

(defn load-main []
  (om/root
   main-view
   st/app-state
   {:target (. js/document (getElementById "app-main"))})
)

(defn load []
  (load-nav)
  (load-main)
  (println "implementing components!!"))
