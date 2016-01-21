(ns todos.components.view-todo
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   [todos.routes :as rts]
   [todos.util :as util]
   ))

(defn parse-value [value attr]
  (cond
    (= attr :date)
    (if (util/date-str? value) value)
    :else
    value))

(defn div-edit-buttons [owner ref todo-id todo-attr]
  (let [button-margin "1em"]
    (dom/div
     nil
     (dom/button
      #js {:onClick #(om/set-state! owner :value "")
           :style #js {:marginRight button-margin}}
      "Clear")
     (dom/button
      #js {:onClick
           (fn []
             (let [value (.. (om/get-node owner ref)
                             -value)]
               (om/set-state! owner :editing nil)
               (-> js/hoodie.store
                   (.update
                    (:todo st/store-types)
                    todo-id
                    (clj->js
                     (hash-map todo-attr (parse-value value todo-attr))))
                   (.fail (fn [err]
                            (println "update error")
                            (js/console.log err)))
                           )))
           :style #js {:marginRight button-margin}}
      "Update")
     (dom/button
      #js {:onClick #(om/set-state! owner :editing nil)}
      "Cancel")
     )))

(defn div-attr-editing
  [owner todo-id todo-attr value
   title-str input-tag input-attrs]
  (let [my-ref "todo-attr-ref"]
    (dom/div
     nil
     (dom/h5 nil title-str)
     (input-tag
      (clj->js
       (merge-with
        ;; no duplicate keys
        #(assert false)
        {:ref my-ref
         :value value
         :onChange #(om/set-state! owner :value (.. % -target -value))
         }
        input-attrs
        )))
     (div-edit-buttons
      owner my-ref todo-id todo-attr)
     )))

(defn div-attr-viewing [owner value title-str]
  (let [edit-onclick
        (fn []
          (om/set-state! owner :editing true)
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
            value)
     )))

(defn attr-view
  [value owner
   {:keys [title-str input-tag input-attrs todo-id todo-attr] :as opts}]
  (reify
      om/IInitState
    (init-state [_]
      {:editing nil
       :value value})
    om/IWillUnmount
    (will-unmount [this]
      (om/set-state! owner :editing nil))
    om/IWillReceiveProps
    (will-receive-props [this next-value]
      (om/set-state! owner :value next-value)
      )
    om/IWillUpdate
    (will-update [this next-value next-state]
      (if (and
           (not (= (:editing (om/get-render-state owner))
                   (:editing next-state)))
           (not (:editing next-state)))
        (om/set-state! owner :value next-value))
      )
    om/IRenderState
    (render-state [this {:keys [editing value]}]
      (if editing
        (div-attr-editing
         owner todo-id todo-attr value
         title-str input-tag input-attrs)
        (div-attr-viewing
         owner value title-str)
        )
      )))

;; todo-attr: the data attribute (key) of the todo (map) to view/edit
(defn view-todo-attr [todo todo-attr
                      title-str input-tag
                      & {:keys [input-attrs]}]
  (om/build
   attr-view (get todo todo-attr)
   {:opts {:title-str title-str
           :input-tag input-tag
           :input-attrs input-attrs
           :todo-id (:id todo)
           :todo-attr todo-attr
           }})
  )

(defn title-attr [owner todo]
  (view-todo-attr
   todo :title "Title" dom/input))

(defn status-attr [todo]
  (dom/div
   nil
   (dom/h5
    #js {:style #js {:marginBottom ".25em"}}
    (dom/span nil "Status: ")
    (dom/span nil
              (if (:done todo) "finished" "unfinished"))
    )))

(defn soon-attr [todo]
  (dom/div
   #js {:style #js {:marginBottom "1.5em"}}
   (domh/labeled-checkbox
    "finish soon" (:soon todo)
    (fn []
      (js/hoodie.store.update
       (:todo st/store-types)
       (:id todo)
       (clj->js {:soon (not (:soon todo))}))))
   ))

(defn date-attr [owner todo]
  (view-todo-attr
   todo :date "Due date" dom/input
   :input-attrs {:type "date"}))

(defn description-attr [owner todo]
  (view-todo-attr
   todo :description "Description" dom/textarea))

(defn delete-button [todo all-todos]
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
   "Delete"))

(defn view-todo-view [data owner]
  (reify
    om/IRender
    (render [this]
      (let [id (:id (:route-params data))
            all-todos (:todos data)
            todo (first (filter #(= id (:id %)) all-todos))]
        (dom/div
         nil
         (title-attr owner todo)
         (status-attr todo)
         (soon-attr todo)
         (date-attr owner todo)
         (description-attr owner todo)
         (delete-button todo all-todos)
         )
        ))))
