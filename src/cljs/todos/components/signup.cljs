(ns todos.components.signup
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   ))


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
      ((domh/center-div)
       (dom/h3 nil "Sign up")
       (domh/input "Username:" "text" "username")
       (domh/input "Password:" "password" "password")
       (domh/input "Repeat password:" "password" "pass-confirm")
       (dom/button
        #js {:onClick #(signup data owner)} "Signup!")
       )
      )))

