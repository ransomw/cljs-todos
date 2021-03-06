(ns todos.components.home
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   )
  (:use
   [todos.components.todo-list :only [todo-list-view
                                      todo-tree-list-view]]
   )
  )

(def header-div-style
  #js {
       :marginBottom "1.5em"
       :display "flex"
       :alignItems "center"
       :justifyContent "space-between"
       })

(defn make-add-new-soon-todo [owner]
  (fn [new-soon-todo]
    (let [prev-soon-todos
          (om/get-state owner :new-soon-todos)
          matching-new-soon-todos
          (filter
           (fn [prev-soon-todo]
             (= (:id prev-soon-todo) (:id new-soon-todo)))
           prev-soon-todos)]
      (assert (> 2 (count matching-new-soon-todos)))
      (if (= 0 (count matching-new-soon-todos))
        (om/set-state!
         owner
         :new-soon-todos
         (concat [new-soon-todo] prev-soon-todos)
         )))))

(defn priority-list-div [priority-todos]
  (dom/div
   nil
   (dom/div
    #js {:style header-div-style}
    (dom/h3 #js {:style #js {:marginBottom "0"}} "priority")
    (dom/button
     #js {:onClick
          (fn []
            (doall (map (fn [todo]
                          (js/hoodie.store.update
                           (:todo st/store-types)
                           (:id todo)
                           (clj->js {:soon nil}))
                          )
                        priority-todos)))}
     "clear"))
   (om/build todo-list-view {:todos-list priority-todos})))

(defn due-soon-div
  [due-todos priority-todos add-new-soon-todo]
  (dom/div
   nil
   (dom/div
    #js {:style header-div-style}
    (dom/h4 #js {:style #js {:marginBottom "0"}} "due soon"))
   (om/build todo-list-view
             {:todos-list
              ;; don't display under both "soon" and "priority",
              ;; already, two items can't be added to priority
              (filter
               (fn [due-todo]
                 true
                 ;; (reduce
                 ;;  (fn [a b] (or a b))
                 ;;  (map (fn [priority-todo]
                 ;;        (not (= (:id due-todo)
                 ;;                (:id priority-todo))))
                 ;;       priority-todos))
                 )
               due-todos)
              :on-todo-sel
              (if (= 0 (count priority-todos))
                add-new-soon-todo)})))

(defn all-todos-div
  [all-todos expand-depth add-new-soon-todo]
  (dom/div
   nil
   (dom/div
    #js {:style header-div-style}
    (dom/h4 #js {:style #js {:marginBottom "0"}} "all todos"))
   (om/build
    todo-tree-list-view
    {:todos-trees
     (vec (st/todo-list-to-tree
           (filter (fn [todo] (not (:done todo))) all-todos)))
     :expand-depth expand-depth}
    {:opts {:on-todo-sel add-new-soon-todo}}
    )
   ))

(defn set-priority-list [owner new-soon-todos]
  (om/build
   todo-list-view
   {:todos-list new-soon-todos
    :on-todo-sel
    (fn [todo]
      (let [prev-soon-todos
            (om/get-state owner :new-soon-todos)
            updated-soon-todos
            (filter (fn [other-todo]
                      (not (= (:id todo)
                              (:id other-todo)))
                      )
                    prev-soon-todos)]
        (assert (= (- (count prev-soon-todos) 1)
                   (count updated-soon-todos)))
        (om/set-state!
         owner :new-soon-todos updated-soon-todos)
        ))
    }))

(defn set-priority-list-div [owner new-soon-todos]
  (dom/div
   nil
   (let [are-new-soon-todos? (not (= 0 (count new-soon-todos)))]
     (dom/div
      #js {:style header-div-style}
      (dom/h3
       (clj->js {:style
                 {:marginBottom "0"}})
       (if are-new-soon-todos?
         "priority todos" "set priority"))
      (if are-new-soon-todos?
        (dom/button
         #js {:onClick
              (fn []
                (doall (map (fn [todo]
                              (js/hoodie.store.update
                               (:todo st/store-types)
                               (:id todo)
                               (clj->js {:soon true}))
                              )
                            new-soon-todos)))}
         "save"))))
   (set-priority-list owner new-soon-todos)))

(defn home-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      {:new-soon-todos []})
    om/IWillReceiveProps
    (will-receive-props [this next-props]
      (om/set-state! owner :new-soon-todos [])
      )
    om/IRenderState
    (render-state [this {:keys [new-soon-todos]}]
      (let [all-todos (:todos data)
            priority-todos
            (filter (fn [todo] (:soon todo)) all-todos)
            due-todos
            (st/sort-by-due-date
             (filter (fn [todo]
                       (and (not (:done todo)) (:date todo)))
                     all-todos))
            add-new-soon-todo
            (make-add-new-soon-todo owner)
            ]
        ((domh/center-div :out-cols "two" :in-cols "eight")
         (if (not (= 0 (count priority-todos)))
           (priority-list-div priority-todos)
           (set-priority-list-div owner new-soon-todos)
           )
         (due-soon-div
          due-todos priority-todos add-new-soon-todo)
         (if (= 0 (count priority-todos))
           (all-todos-div
            all-todos
            (:expand-depth (:config data))
            add-new-soon-todo))
         )
        ))))
