(ns todos.state
  (:require
   [clojure.string :as str]
  ))

(def store-types
  {:todo "todo"})

(defonce app-state
  (atom
   {:route
    {:path "/"
     :params {}}
    :username js/hoodie.account.username
    :todos []
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

(defn set-route [path params]
  (update-atom-dict
   app-state :route
   {:path path :params params}))

(js/hoodie.account.on
 "signin"
 #(update-atom-dict app-state :username js/hoodie.account.username))

(js/hoodie.account.on
 "signout"
 #(update-atom-dict app-state :username js/hoodie.account.username))

(def my-todos nil)

;; todo: use transit instead of js->clj
(defn update-todos []
  (-> js/hoodie.store
      (.findAll (:todo store-types))
      (.done
       (fn [todos]
         (update-atom-dict
          app-state :todos
          (js->clj todos :keywordize-keys true))))
      (.fail (fn [err]
               (println "update-todos error")
               (js/console.log err)))
  ))

(js/hoodie.store.on
 (str/join [(:todo store-types) ":add"])
 update-todos)

(js/hoodie.store.on
 (str/join [(:todo store-types) ":update"])
 update-todos)

(js/hoodie.store.on
 (str/join [(:todo store-types) ":remove"])
 update-todos)

(defn update-state []
  (update-todos))
