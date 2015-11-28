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
         login-path
         new-todo-path
         all-todos-path
         view-todo-path
         re-login-path)

(defroute home-path "/" []
  (println "home-path")
  (st/set-route (home-path) {})
  )

(defroute login-path "/login" []
  (println "login-path")
  (println (login-path))
  (st/set-route (login-path) {})
  )

(defroute new-todo-path "/new" []
  (println "new-todo-path")
  (st/set-route (new-todo-path) {})
  )

(defroute all-todos-path "/todos" []
  (println "all-todos-path")
  (st/set-route (all-todos-path) {})
  )

(defroute view-todo-path "/todos/:id" [id]
  (println "view-todo-path")
  (st/set-route (view-todo-path) {:id id})
  )

(defroute re-login-path "/pass" []
  (st/set-route (re-login-path) {})
  )

;; like a redirect for the client-side
(defn navigate-to [path]
  (println "navigate-to function")
  (secretary/dispatch! path)
  (js/window.open path "_self")
  ;;;;; ?????
  ;;; goog closure History Navigate event doesn't fire
  ;;; if pushState is used to change URL
  ;; (js/window.history.pushState #js {} "" path)
  ;; (js/window.history.pushState #js {} "" path)
  ;; (js/window.history.back)
  )

(defn logout []
  (-> js/hoodie.account
      (.signOut)
      (.done
       (fn []
         (navigate-to (home-path))))
      (.fail
       (fn [err]
         (if (= (.-status err) 401)
           (navigate-to (re-login-path))
           (do
             (println "signout err")
             (js/console.log err)
             (js/alert "signout error, see log")))))))

(defroute logout-path "/logout" []
  (println "logout-path")
  (logout))

(defroute "*" []
  (println "lost!"))

;;;; todo: get rid of all the closure events stuff
;; just listen to all click events and prevent default if URL matches
;; will also allow using pushState and removing hash from URLS
(defn start-routing []
  (let [h (History.)]
    (println "start-routing")
    (events/listen
     h EventType.NAVIGATE
     (fn [ev]
       (println "navigate event")
       (secretary/dispatch! (.-token ev))))
    (doto h (.setEnabled true))
    ))
