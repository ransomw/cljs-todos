(ns todos.components.config
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   ))

(defn config-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      {:editing nil})
    om/IRenderState
    (render-state [this {:keys [editing]}]
      (let [config (:config data)]
        ((domh/center-div :out-cols "two" :in-cols "eight")
         (dom/h3 nil "config")
         (if (= editing :expand-depth)
           (let [item-style #js {:marginBottom "0"
                                 :marginRight "1rem"}
                 expand-depth-ref "expand-depth"]
             (dom/div
              #js {:style #js {:display "flex"
                               :alignItems "center"
                               :justifyContent "flex-start"}}
              (dom/span #js {:style item-style} "expand depth")
              (dom/input
               (clj->js {:type "number"
                         :style (merge {:width "4em"} (js->clj item-style))
                         :defaultValue (:expand-depth config)
                         :ref expand-depth-ref
                         ;;https://facebook.github.io/react/docs/events.html
                         :onChange (fn [e]
                                     ;; todo: allow only positive vals
                                     )}))
              (dom/button
               #js {:style item-style
                    :onClick
                    (fn [e]
                      (let [new-expand-depth
                            (js/parseInt
                             (.. (om/get-node owner expand-depth-ref)
                                 -value))]
                        (println new-expand-depth)
                        (if (< new-expand-depth 1)
                          (js/alert "expand depth must be positive")
                          (do
                            (js/hoodie.store.update
                             (:config st/store-types)
                             st/id-config
                             #js {:expand-depth new-expand-depth}
                             )
                            (om/set-state! owner :editing nil)))))}
               "save")
              ))
           (dom/span
            #js {:onClick #(om/set-state! owner :editing :expand-depth)}
            (dom/span nil "expand depth: ")
            (dom/span nil (:expand-depth config)))
           )
         )))))
