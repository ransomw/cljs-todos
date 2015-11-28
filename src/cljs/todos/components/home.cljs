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
      (let [header-div-style
            #js {
                 :marginBottom "1.5em"
                 :display "flex"
                 :alignItems "center"
                 :justifyContent "space-between"
                 }
            all-todos (:todos data)
            priority-todos
            (filter (fn [todo] (:soon todo)) all-todos)
            due-todos
            (st/sort-by-due-date
             (filter (fn [todo]
                       (and (not (:done todo)) (:date todo)))
                     all-todos))
            add-new-soon-todo
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
                   ))))
            ]
        ((domh/center-div :out-cols "two" :in-cols "eight")
         (if (not (= 0 (count priority-todos)))
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
            (om/build todo-list-view priority-todos))
           (dom/div
            nil
            (dom/div
             #js {:style header-div-style}
             (dom/h3 #js {:style #js {:marginBottom "0"}}
                     (if (= 0 (count new-soon-todos))
                       "set priority" "priority todos"))
             (if (not (= 0 (count new-soon-todos)))
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
                "save")))
            (om/build
             todo-list-view new-soon-todos
             {:opts {:on-todo-sel
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
                              ))}}))
         )
        (dom/div
         nil
         (dom/div
          #js {:style header-div-style}
          (dom/h4 #js {:style #js {:marginBottom "0"}} "due soon"))
         ;; todo: bug: set priority, save, and others don't update
         ;;   i.e., opts don't propegate to all todo-item-list-view s,
         ;;         only to those newly flagged as priorities
         ;; clicking around in the navigation clears this up
         ;; to fix, try a forced re-render (re-build?) on "save" click
         (om/build todo-list-view due-todos
                   {:opts {:on-todo-sel
                           (if (= 0 (count priority-todos))
                             add-new-soon-todo)}}
                   ))
         (if (= 0 (count priority-todos))
           (dom/div
            nil
            (dom/div
             #js {:style header-div-style}
             (dom/h4 #js {:style #js {:marginBottom "0"}} "all todos"))
            (om/build
             todo-tree-list-view
             (vec (st/todo-list-to-tree
                   (filter (fn [todo] (not (:done todo))) all-todos)))
             {:opts {:on-todo-sel add-new-soon-todo}}
             )
            ))
         )
        ))))
