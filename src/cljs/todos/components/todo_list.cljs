(ns todos.components.todo-list
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   [todos.routes :as rts]
   [todos.util :as util]
   ))

(defn todo-div [todo & {:keys [pre-elem on-todo-sel]}]
  (dom/div
   #js {:style #js {:width "100%"
                    :borderBottom "solid #aaa .1em"
                    :paddingBottom ".5em"
                    :display "flex"
                    :alignItems "center"
                    :justifyContent "space-between"
                    }}
   pre-elem
   (dom/a
    (clj->js {:href (rts/view-todo-path {:id (:id todo)})
              })
    (:title todo))
   (dom/span nil (util/date-display-str (:date todo)))
   (if on-todo-sel
     (dom/button
      (clj->js {:style {:marginBottom "0"}
                ;; todo: dissoc child todos
                :onClick #(on-todo-sel todo)})
      "X")
     (domh/checkbox (:done todo)
                    (fn [e]
                      (js/hoodie.store.update
                       (:todo st/store-types)
                       (:id todo)
                       #js {:done (not (:done todo))}))))
   ))

(defn todo-item-list-view
  [{:keys [todo on-todo-sel]} owner]
  ;; [todo owner {:keys [on-todo-sel] :as opts}]
  (reify
      om/IRender
    (render [this]
      (dom/li
       nil
       (todo-div todo :on-todo-sel on-todo-sel)
       ))))

(defn todo-list-view
  [{:keys [todos-list on-todo-sel]} owner]
  ;; [todos-list owner {:keys [on-todo-sel] :as opts}]
  (reify
    om/IRender
    (render [this]

      ;; (println "todo-list-view render")
      ;; (println todos-list)
      ;; (println (first todos-list))

      (apply dom/ul
             #js {:style #js{:listStyle "none"}}
             (om/build-all
              todo-item-list-view
              (map (fn [todo]
                     {:todo todo
                      :on-todo-sel on-todo-sel})
                   todos-list)

              ;; {:opts {:on-todo-sel on-todo-sel}}

              ))
      )))



(declare todo-tree-list-view)

(defn todo-item-tree-view
  [todos-tree owner {:keys [on-todo-sel] :as opts}]
  (reify
      om/IInitState
    (init-state [_]
      {:expand true})
    om/IRenderState
    (render-state [this {:keys [expand]}]
      (dom/li
       nil
       (todo-div
        todos-tree
        :pre-elem
        (dom/span
         #js {:onClick
              (fn []
                (om/set-state! owner :expand (not expand)))
              :style #js {:height "100%"}}
         (if expand "\u00a0\\/\u00a0" "\u00a0>\u00a0"))
        :on-todo-sel on-todo-sel)
       (if (st/has-sub-todos todos-tree)
         (om/build
          todo-tree-list-view (:sub-todos todos-tree)
          {:opts {:on-todo-sel on-todo-sel}
           :state {:hidden (not expand)}})
         )
       )
      )))

(defn todo-tree-list-view [todos-tree owner
                           {:keys [on-todo-sel] :as opts}]
  (reify
      om/IInitState
    (init-state [_]
      {:hidden false})
    om/IRenderState
    (render-state [this {:keys [hidden]}]
      (apply dom/ul
             #js {:hidden hidden
                  :style #js{:listStyle "none"}}
             (om/build-all
              todo-item-tree-view todos-tree
              {:opts {:on-todo-sel on-todo-sel}}))
      )))
