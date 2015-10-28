(ns todos.routes
  (:require
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as events]
   [todos.state :as st]
   )

  (:import [goog.history EventType]
           [goog History])
  )

(secretary/set-config! :prefix "#")

(declare home-path
         login-path)

(defroute home-path "/" []
  (println "home-path")
  (st/set-route (home-path) {})
  )

(defroute login-path "/login" []
  (println "login-path")
  (println (login-path))
  (st/set-route (login-path) {})
  )

(defn logout []
  (-> js/hoodie.account
      (.signOut)
      (.done
       (fn []
         (secretary/dispatch! (home-path))))
      (.fail
       (fn [err]
         (println "signout err")
         (js/console.log err)
         (js/alert "signout error, see log")))))

(defroute logout-path "/logout" []
  (println "logout-path")
  (logout))

(defroute "*" []
  (println "lost!"))

(defn start-routing []
  (let [h (History.)]
    (println "start-routing")
    (events/listen
     h EventType.NAVIGATE
     (fn [ev]
       (secretary/dispatch! (.-token ev))))
    (doto h (.setEnabled true))
    ))
