(ns todos.components.login
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.routes :as rts]
   ))

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
      ((domh/center-div)
       (dom/h3 nil "Login")
       (domh/input "Username" "text" "username")
       (domh/input "Password" "password" "password")
       (dom/button
        #js {:onClick #(login data owner)} "Login")
       )
      )))

