(ns todos.components
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   [todos.routes :as rts]
   ))

(defn nav-view [data owner]
  (om/component
   (dom/div
    nil
    (dom/div
     #js {:className "brand"}
     (dom/a #js {:href "#/"} "todos"))
    (dom/ul
     #js {:className "actions"}
     (if (:username data)
       (dom/li
        nil
        (dom/a
         #js {:href "#/logout"
              :className "button"}
         "Logout"))
       (dom/li
        nil
        (dom/a
         #js {:href "#/login"
              :className "button"}
         "Login"))
       ))
    )
   ))

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
                 (js/alert "signed in!")
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
    (render [_]
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
