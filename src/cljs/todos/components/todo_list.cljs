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
  (reify
      om/IRender
    (render [this]
      (dom/li
       nil
       (todo-div todo :on-todo-sel on-todo-sel)
       ))))

(defn todo-list-view
  [{:keys [todos-list on-todo-sel]} owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/ul
             #js {:style #js{:listStyle "none"}}
             (om/build-all
              todo-item-list-view
              (map (fn [todo]
                     {:todo todo
                      :on-todo-sel on-todo-sel})
                   todos-list)))
      )))



(declare todo-tree-list-view)

(defn todo-item-tree-view
  [{:keys [todos-tree expand-depth]} owner
   {:keys [on-todo-sel depth] :as opts}]
  (reify
      om/IInitState
    (init-state [_]
      {:expand (or (not (st/has-sub-todos todos-tree))
                   (< depth expand-depth))})
    om/IRenderState
    (render-state [this {:keys [expand]}]
      (dom/li
       nil
       (todo-div
        todos-tree
        :pre-elem
        (dom/a
         #js {:onClick
              (fn [e]
                (.preventDefault e)
                (om/set-state! owner :expand (not expand)))
              :href ""
              :style #js {:height "100%"
                          :color "black"}}
         (if expand "\u00a0\\/\u00a0" "\u00a0>\u00a0"))
        :on-todo-sel on-todo-sel)
       (if (st/has-sub-todos todos-tree)
         (om/build
          todo-tree-list-view
          {:todos-trees (:sub-todos todos-tree)
           :expand-depth expand-depth}
          {:opts {:on-todo-sel on-todo-sel
                  :depth (+ 1 depth)}
           :state {:hidden (not expand)}})
         )
       )
      )))

;; todo: possible to set default opts for components?
;;       (default depth to 0)
(defn todo-tree-list-view
  [{:keys [todos-trees expand-depth]} owner
   {:keys [on-todo-sel depth] :as opts}]
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
              todo-item-tree-view
              (map (fn [todos-tree]
                     {:todos-tree todos-tree :expand-depth expand-depth})
                   todos-trees)
              {:opts {:on-todo-sel on-todo-sel
                      :depth (if depth depth 1)}}))
      )))
