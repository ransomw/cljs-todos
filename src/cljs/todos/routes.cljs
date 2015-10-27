(ns todos.routes
  (:require
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as events]
   [todos.components :as comp]
   )

  (:import [goog.history EventType]
           [goog History])
  )

(secretary/set-config! :prefix "#")

(defroute home-path "/" []
  (println "home-path")
  (comp/load-home))

(defroute login-path "/login" []
  (println "login-path")
  (comp/load-login))

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
     ;; #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true))
    ;; (secretary/dispatch! (home-path))
    ))

(defn load []
  (comp/load-nav)
  ;; (secretary/dispatch! (home-path))
  (println "implementing routes"))
