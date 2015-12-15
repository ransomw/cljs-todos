(ns todos.components.all-todos
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   )
  (:use
   [todos.components.todo-list :only [todo-tree-list-view]]
   ))

(defn all-todos-list [todos expand-depth show-done]
  (om/build
   todo-tree-list-view
   {:todos-trees
    (vec (st/todo-list-to-tree
          (filter
           (if show-done
             (fn [todo] true)
             (fn [todo] (not (:done todo))))
           todos)))
    :expand-depth expand-depth}))

(defn all-todos-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      {:show-done false})
    om/IRenderState
    (render-state [this {:keys [show-done]}]
      (let [todos (:todos data)]
        ((domh/center-div :out-cols "two" :in-cols "eight")
         (dom/h3
          #js {:style #js {:textAlign "center"
                           :marginBottom ".25em"}}
          "all todos")
         (dom/div
          #js {:style #js {:marginBottom "3em"
                           :display "flex"
                           :justifyContent "center"
                           }}
          (domh/labeled-checkbox
           "show completed" show-done
           #(om/set-state!
             owner :show-done (not show-done)))
          )
         (all-todos-list todos (:expand-depth (:config data)) show-done)
         )
        ))))
