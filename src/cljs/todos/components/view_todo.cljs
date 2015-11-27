(ns todos.components.view-todo
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.component-helpers :as comh]
   [todos.state :as st]
   [todos.routes :as rts]
   ))


(defn view-todo-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      {:editing nil})
    om/IWillUpdate
    (will-update [this next-props next-state]

      (println "view todo view update")

      (let [editing (:editing next-props)
            allowed-editing [:title :done :date :description]]
        (if editing
          (assert (some #{editing} allowed-editing)))
        ))
    om/IWillUnmount
    (will-unmount [this]
      ;; todo: unmount callback not getting called on navigation
      ;; perhaps necessary to om/build components in main-view?
      (println "view todo view unmount")
      (om/set-state! owner :editing nil)
      )
    om/IRenderState
    (render-state [this {:keys [editing]}]
      (let [id (:id (:params (:route data)))
            todo (first (filter #(= id (:id %)) (:todos data)))]
        (dom/div
         nil
         (comh/view-todo-attr
          owner todo "title" :title editing :title
          "Title" dom/input)
         (dom/div
          nil
          (dom/h5
           nil
           (dom/span nil "Status: ")
           (dom/span nil
                     (if (:done todo) "finished" "unfinished"))
           )
          )
         (comh/view-todo-attr
          owner todo "date" :date editing :date
          "Due date" dom/input :input-attrs {:type "date"})
         (comh/view-todo-attr
          owner todo "description" :description editing :description
          "Description" dom/textarea)
         (dom/button
          #js {:onClick
               (fn []
                 (if (js/confirm "delete todo?")
                   (do
                     (rts/navigate-to (rts/home-path))
                     ;; todo: handle delete with child todos
                     (js/hoodie.store.remove
                      (:todo st/store-types)
                      (:id todo))
                     )))}
          "Delete"
          )
         )
        ))))
