(ns todos.state
  (:require
   [clojure.string :as str]
   [todos.util :as util]
  ))

(def store-types
  {:todo "todo"
   :config "config"})

(defonce app-state
  (atom
   {:route
    {:path "/"
     :params {}}
    :username js/hoodie.account.username
    :todos []
    :config {:expand-depth 2
             :soon-days 7}}))

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

(def id-config "aaaaaaa")

(defn update-config []
  (-> js/hoodie.store
      (.find (:config store-types) id-config)
      (.done
       (fn [config]
         (println "updating config")
         (update-atom-dict
          app-state :config
          (js->clj config :keywordize-keys true))))
      (.fail (fn [err]
               (println "error updating config")
               (println err)
               (js/alert "error updating config")
               ))
   ))

(defn init-config []
  (-> js/hoodie.store
      (.findOrAdd
       (:config store-types)
       id-config
       #js {:expand-depth 2
            :soon-days 7})
      (.done (fn [config]
               (println "initialized config")
               (update-config)))
      (.fail (fn [err]
               (assert false
                       (str/join ["error initializing config: "
                                  (.toString err)]))))
      ))

(js/hoodie.store.on
 (str/join [(:config store-types) ":update"])
 update-config
 )

(js/hoodie.store.on
 (str/join [(:config store-types) ":remove"])
 #(assert false))


(defn init-state []
  (init-config)
  (update-todos))

(defn todo-has-children? [some-todo other-todos]
  (not (= 0 (count
             (filter (fn [some-other-todo]
                       (= (:id some-todo)
                          (:parent-id some-other-todo)))
                     other-todos)))))

;; todo: this... could be cleaner, faster, but
;;   it's correct, so test and profile first
(defn todo-list-to-tree [todos]
  "return trees of todos rather than a flat list structure.  all keys are the same, except :parent-id is replaced with :sub-todos vec"
  (let [todos-no-parent (filter
                         (fn [todo] (not (:parent-id todo)))
                         todos)
        todos-w-parent (filter
                         (fn [todo] (:parent-id todo))
                         todos)]
    (map (fn [todo-no-parent]
           (assoc
            todo-no-parent
            :sub-todos
            (vec
             (todo-list-to-tree
              (map (fn [todo-w-parent]
                     (if (= (:id todo-no-parent)
                            (:parent-id todo-w-parent))
                       (dissoc todo-w-parent :parent-id)
                       todo-w-parent))
                   todos-w-parent)))))
         todos-no-parent)))

(defn has-sub-todos [todos-tree]
  (let [sub-todos (:sub-todos todos-tree)]
    (if sub-todos
      (not (= 0 (count sub-todos))))))

(defn sort-by-due-date [todos]
  (sort (fn [todoA todoB]
          (let [dateA (:date todoA)
                dateB (:date todoB)]
            (util/date-str-leq dateA dateB)
          ))
        todos))

;; helper fn
(defn h-get-sub-todos-tree [todos-trees todo]
  (let [todo-tree-node
        (first (filter (fn [tree-node] (= (:id tree-node) (:id todo)))
                       todos-trees))]
    (if todo-tree-node
      (:sub-todos todo-tree-node)
      (first
       (filter
        #(not (= nil %))
        (map (fn [tree-node]
               (h-get-sub-todos-tree (:sub-todos tree-node) todo))
             todos-trees)))
      )))

;; todo: faster?
(defn get-sub-todos-tree [todos todo]
  (h-get-sub-todos-tree (todo-list-to-tree todos) todo))
