(ns todos.components.new-todo
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   [todos.routes :as rts]
   [todos.util :as util]
   )
  (:use
   [todos.components.todo-list :only [todo-tree-list-view]]
   ))

(defn new-todo [owner & {:keys [parent-id]}]
  (let [
        title (.-value (om/get-node owner "title"))
        description (.-value (om/get-node owner "description"))
        date-input (.-value (om/get-node owner "date"))
        date (if (util/date-str? date-input) date-input)
        ]
    (-> js/hoodie.store
        (.add
         (:todo st/store-types)
         #js {:title title
              :date date
              :done false
              :description description
              :parent-id parent-id})
        (.done (fn [todo]
                 (rts/navigate-to (rts/home-path))))
        (.fail (fn [err]
                 (println "error adding todo")
                 (js/console.log err)
                 (js/alert "error adding todo")))
        )
    ))

(defn select-parent-div [data owner parent-todo on-todo-sel]
  (dom/div
   nil
   (dom/label nil "Select parent todo")
   (dom/div
    #js {:style #js {:width "100%"
                     :marginBottom "2em"
                     :display "flex"
                     :alignItems "center"
                     :justifyContent "space-between"
                     }}
    (dom/span
     nil
     (if parent-todo
       (or (:title parent-todo) "top-level todo")
       "no parent selected"))
    (if parent-todo
      (dom/button
       (clj->js {:style {:marginBottom "0"}
                 :onClick #(om/set-state! owner :parent-todo nil)})
       "Clear")
      (dom/button
       (clj->js {:style {:marginBottom "0"}
                 :onClick #(om/set-state! owner :parent-todo {})})
       "top-level")))
   (if (not parent-todo)
     (om/build
      todo-tree-list-view
      {:todos-trees
       (vec (st/todo-list-to-tree
             (filter (fn [todo] (not (:done todo)))
                     (:todos data))))
       :expand-depth (:expand-depth (:config data))}
      {:opts {:on-todo-sel on-todo-sel}}))
   )
  )

(defn new-todo-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      {:parent-todo nil}
      )
    om/IRenderState
    (render-state [this {:keys [parent-todo]}]
      (let [on-todo-sel
            (fn [todo]
              (om/set-state! owner :parent-todo todo))]
        ((domh/center-div :out-cols "two" :in-cols "eight")
         (dom/h3 nil "new todo")
         (domh/input "Todo" "text" "title")
         (domh/input "Due" "date" "date")
         (dom/div
          nil
          (dom/label nil "Description")
          (dom/textarea
           #js {:ref "description"}))
         (select-parent-div data owner parent-todo on-todo-sel)
         (dom/button
          #js {:onClick
               #(new-todo owner :parent-id (:id parent-todo))
               } "Add")
         )
        ))))
