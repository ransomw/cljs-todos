(ns todos.components.view-todo
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   [todos.routes :as rts]
   ))

(defn edit-buttons [owner todo ref attr]
  (dom/div
   nil
   (dom/button
    #js {:onClick (fn []
                    (om/set-state! owner :editing nil)
                    (js/hoodie.store.update
                     (:todo st/store-types)
                     (:id todo)
                     (clj->js
                      (hash-map attr
                                (.. (om/get-node owner ref)
                                    -value)))))
         :style #js {:marginRight "1em"}}
    "Update")
   (dom/button
    #js {:onClick #(om/set-state! owner :editing nil)}
    "Cancel")
   ))

(defn view-todo-attr [owner todo ref attr editing editing-key
                      title-str input-tag
                      & {:keys [input-attrs]}]
  (if (= editing editing-key)
    (dom/div
     nil
     (dom/h5 nil title-str)
     (input-tag
      (clj->js
       (merge-with
        ;; no duplicate keys
        #(assert false)
        {:ref ref
         :defaultValue (get todo attr)
         }
        input-attrs
        )))
     (edit-buttons
      owner todo ref attr)
     )
    (let [edit-onclick
          (fn []
            (om/set-state! owner :editing editing-key)
            )]
      (dom/div
       nil
       (dom/h5
        nil
        (dom/span nil title-str)
        (dom/span #js {:onClick edit-onclick
                       :style #js {:marginLeft "1em"}}
                  "[edit]"))
       (dom/p #js {:onClick edit-onclick}
              (get todo attr))
       )
      )
    )
)

(defn view-todo-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      {:editing nil})
    om/IWillUpdate
    (will-update [this next-props next-state]
      (let [editing (:editing next-props)
            allowed-editing [:title :done :date :description]]
        (if editing
          (assert (some #{editing} allowed-editing)))))
    om/IWillUnmount
    (will-unmount [this]
      (om/set-state! owner :editing nil))
    om/IRenderState
    (render-state [this {:keys [editing]}]
      (let [id (:id (:route-params data))
            all-todos (:todos data)
            todo (first (filter #(= id (:id %)) all-todos))]
        (dom/div
         nil
         (view-todo-attr
          owner todo "title" :title editing :title
          "Title" dom/input)
         (dom/div
          nil
          (dom/h5
           #js {:style #js {:marginBottom ".25em"}}
           (dom/span nil "Status: ")
           (dom/span nil
                     (if (:done todo) "finished" "unfinished"))
           )
          )
         (dom/div
          #js {:style #js {:marginBottom "1.5em"}}
          (domh/labeled-checkbox
           "finish soon" (:soon todo)
           (fn []
             (js/hoodie.store.update
              (:todo st/store-types)
              (:id todo)
              (clj->js {:soon (not (:soon todo))}))))
          )
         (view-todo-attr
          owner todo "date" :date editing :date
          "Due date" dom/input :input-attrs {:type "date"})
         (view-todo-attr
          owner todo "description" :description editing :description
          "Description" dom/textarea)
         (dom/button
          #js {:onClick
               (fn []
                 (if (st/todo-has-children? todo all-todos)
                   (js/alert "cannot delete: has dependent todos")
                   (if (js/confirm "delete todo?")
                     (do
                       (rts/navigate-to (rts/home-path))
                       (js/hoodie.store.remove
                        (:todo st/store-types)
                        (:id todo))
                       ))))}
          "Delete"
          )
         )
        ))))
