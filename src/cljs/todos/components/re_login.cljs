(ns todos.components.re-login
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.routes :as rts]
   ))

(defn re-login [data owner]
  (let [
        password (.-value (om/get-node owner "password"))
        ]
    (-> js/hoodie.account
        (.signIn js/hoodie.account.username
                 password #js {:moveData true})
        (.done (fn [login-username]
                 (rts/navigate-to (rts/home-path))))
        (.fail (fn [err]
                 (println "signin error")
                 (js/console.log err)
                 (js/alert "login failed")))
    )
  ))

(defn re-login-view [data owner]
  (reify
      om/IRender
    (render [this]
      ((domh/center-div)
       (dom/h3 nil "Verify password")
       (dom/h5 nil "to sync local data with server")
       (domh/input "Password" "password" "password")
       (dom/button
        #js {:onClick #(re-login data owner)} "Confirm")
       )
      )))
