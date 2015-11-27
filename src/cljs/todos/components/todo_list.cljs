(ns todos.components.todo-list
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.state :as st]
   [todos.routes :as rts]
   [todos.util :as util]
   ))


;;;; todo: use :opts arg to om/build rather than the make-*-view pattern

(defn make-todo-item-view
  "create a todo item component.  on-todo-sel argument will be called with the todo as an arg when the the given todo is clicked, and marking done will be disabled when on-todo-sel is present"
  [on-todo-sel]
  (fn [todo owner]
    (reify
        om/IInitState
      (init-state [_]
        (let [hidden false
              expand true]
          {:expand expand
           :sub-comps
           (om/build-all
            (make-todo-item-view on-todo-sel)
            (:sub-todos todo)
            {:state {:hidden (or hidden (not expand))}})
           }))
      om/IWillUpdate
      (will-update [this next-props next-state]
        (let [hide-sub-comps? (or (not (:expand next-state))
                                  (:hidden next-state))]

          (println "todo item view update")

          (doall
           (map
            (fn [sub-comp]
              (let [sub-comp-owner
                    (.. sub-comp -props -children -owner)]
                (om/set-state! sub-comp-owner :hidden hide-sub-comps?)
              ))
            (:sub-comps next-state)))))
      om/IRenderState
      (render-state [this {:keys [expand hidden sub-comps]}]
        (dom/li
         (clj->js {:hidden hidden})
         (dom/div
          (clj->js {:style {:width "100%"
                            :borderBottom "solid #aaa .1em"
                            :paddingBottom ".5em"
                            :display "flex"
                            :alignItems "center"
                            :justifyContent "space-between"
                            }})
          (dom/span
           #js {:onClick
                (fn []

                  (println "toggle expand click")
                  (js/console.log owner)
                  ;; (println (om/get-state owner))
                  (js/console.log (.. owner
                                      -_reactInternalInstance
                                      -_context))
                  (js/console.log (.. owner
                                      -state
                                      -__om_pending_state))


                  (om/set-state! owner :expand (not expand)))
                :style #js {:height "100%"}}
           (if expand "\u00a0\\/\u00a0" "\u00a0>\u00a0"))
          (dom/a
           (clj->js {:href (rts/view-todo-path {:id (:id todo)})
                     })
           (:title todo))
          (dom/span nil (util/date-display-str (:date todo)))
          (if on-todo-sel
            (dom/button
             (clj->js {:style {:marginBottom "0"}
                       :onClick #(on-todo-sel todo)})
             "X")
            (domh/checkbox (:done todo)
                           #(js/hoodie.store.update
                             (:todo st/store-types)
                             (:id todo)
                             #js {:done (not (:done todo))})))
          )
         (if (st/has-sub-todos todo)
           (apply dom/ul
                  (clj->js {:style {:listStyle "none"}})
                  sub-comps
                  ))
         )
        ))))

(defn make-todo-list-view
  " create a todo list component.  optional on-todo-sel key argument will be called with the todo as an arg when the the given todo is clicked, and marking done will be disabled when on-todo-sel is present"
  [& {:keys [on-todo-sel]}]
  (fn [todos owner]
    (reify
        om/IRender
      (render [this]
        (apply dom/ul
               (clj->js {:style {:listStyle "none"}})
               (om/build-all
                (make-todo-item-view on-todo-sel)
                (vec (st/todo-list-to-tree todos))))
        ))))

