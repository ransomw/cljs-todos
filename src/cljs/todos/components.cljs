(ns todos.components
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   ))

(def app-state (atom {:username js/hoodie.account.username
                      :mstr "Hi there!"}))

(defn update-atom-dict [atom-dict key val]
  (swap!
   atom-dict
   (fn [m]
     (into
      {}
      (map
       (fn [[k v]]
         (if (= k key)
           [k val]
           [k v])) m))))
  )

(js/hoodie.account.on
 "signin"
 #(update-atom-dict app-state :username js/hoodie.account.username))

(js/hoodie.account.on
 "signout"
 #(update-atom-dict app-state :username js/hoodie.account.username))

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
  (om/root nav-view app-state
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
                 (js/alert "signed in!")))
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

(defn load-home []
  (om/root
   (fn [data owner]
     (if (:username data)
       (home-view data owner)
       (signup-view data owner)))
   app-state
   {:target (. js/document (getElementById "app-main"))})
  )

(defn load-login []
  (om/root login-view app-state
   {:target (. js/document (getElementById "app-main"))})
  )

(defn load []
  (load-nav)
  (load-home)
  (println "implementing components!!"))
