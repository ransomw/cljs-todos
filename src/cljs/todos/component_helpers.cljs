(ns todos.component-helpers
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.state :as st]
   ))

;;;;; view-todo-view

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

            (println "edit on click")

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
