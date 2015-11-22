(ns todos.components
  (:require
   [clojure.string :as str]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.component-helpers :as comh]
   [todos.state :as st]
   [todos.routes :as rts]
   [todos.util :as util]
   ))

(defn nav-view [data owner]
  (let [username (:username data)]
    (om/component
     (dom/div
      nil
      (dom/div
       #js {:className "brand"}
       (dom/a #js {:href "#/"} "todos"))
      (if username
        (dom/ul
         #js {:className "locations"}
         (domh/li-link (rts/new-todo-path) "new todo")
         ))
      (dom/ul
       #js {:className "actions"}
       (if username
         (domh/li-link (rts/logout-path) "Logout" :a-class "button")
         (domh/li-link (rts/login-path) "Login" :a-class "button")
         ))
      )
     )))

(defn load-nav []
  (om/root nav-view st/app-state
           {:target (. js/document (getElementById "nav-main"))})
  )

(defn signup [data owner]
  (let [
        username (.-value (om/get-node owner "username"))
        password (.-value (om/get-node owner "password"))
        pass-confirm (.-value (om/get-node owner "pass-confirm"))
        ]
    (if (= password pass-confirm)
      (-> js/hoodie.account
          (.signUp username password)
          (.done (fn [new-username]
                   (js/alert "signed up!")))
          (.fail (fn [err]
                   (println "signup error")
                   (js/console.log err)
                   (js/alert "sign up failed"))))
      (js/alert "passwords don't match")
    )
  ))

(defn signup-view [data owner]
  (reify
      om/IRender
    (render [_]
      ((domh/center-div)
       (dom/h3 nil "Sign up")
       (domh/input "Username:" "text" "username")
       (domh/input "Password:" "password" "password")
       (domh/input "Repeat password:" "password" "pass-confirm")
       (dom/button
        #js {:onClick #(signup data owner)} "Signup!")
       )
      )))

(defn login [data owner]
  (let [
        username (.-value (om/get-node owner "username"))
        password (.-value (om/get-node owner "password"))
        ]
    (-> js/hoodie.account
        (.signIn username password #js {:moveData true})
        (.done (fn [login-username]
                 (rts/navigate-to (rts/home-path))))
        (.fail (fn [err]
                 (println "signin error")
                 (js/console.log err)
                 (js/alert "login failed")))
    )
  ))

(defn login-view [data owner]
  (reify
      om/IRender
    (render [this]
      ((domh/center-div)
       (dom/h3 nil "Login")
       (domh/input "Username" "text" "username")
       (domh/input "Password" "password" "password")
       (dom/button
        #js {:onClick #(login data owner)} "Login")
       )
      )))

(defn re-login [data owner]
  (let [
        password (.-value (om/get-node owner "password"))
        ]
    (-> js/hoodie.account
        (.signIn js/hoodie.account.username
                 password #js {:moveData true})
        (.done (fn [login-username]
                 (rts/navigate-to (rts/home-path))))
        (.fail (fn [err]
                 (println "signin error")
                 (js/console.log err)
                 (js/alert "login failed")))
    )
  ))

(defn re-login-view [data owner]
  (reify
      om/IRender
    (render [this]
      ((domh/center-div)
       (dom/h3 nil "Verify password")
       (dom/h5 nil "to sync local data with server")
       (domh/input "Password" "password" "password")
       (dom/button
        #js {:onClick #(re-login data owner)} "Confirm")
       )
      )))

;; todo: don't put a border after the last elem
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
           #js {:onClick (fn []

                           (println "toggle expand click")

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

(defn home-view [data owner]
  (reify
      om/IRender
    (render [this]
      ((domh/center-div :out-cols "two" :in-cols "eight")
       (om/build (make-todo-list-view) (:todos data))
       )
      )))

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

(defn new-todo-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      {:parent-todo nil}
      )
      om/IRenderState
    (render-state [this {:keys [parent-todo]}]
      ((domh/center-div :out-cols "two" :in-cols "eight")
       (dom/h3 nil "New todo")
       (domh/input "Todo" "text" "title")
       (domh/input "Due" "date" "date")
       (dom/div
        nil
        (dom/label nil "Description")
        (dom/textarea
         #js {:ref "description"}))
       (dom/div
        nil
        (dom/label nil "Select parent todo")
        (dom/div
         (clj->js {:style {:width "100%"
                           :marginBottom "2em"
                           :display "flex"
                           :alignItems "center"
                           :justifyContent "space-between"
                           }})
         (dom/span nil (if parent-todo
                         (:title parent-todo) "No parent selected"))
         (dom/button
          (clj->js {:style {:marginBottom "0"}
                    :onClick #(om/set-state! owner :parent-todo nil)})
          "Clear"))
        (om/build
         (make-todo-list-view
          :on-todo-sel (fn [todo]
                         (om/set-state! owner :parent-todo todo)))
         (:todos data))
        )
       (dom/button
        #js {:onClick (fn []
                        (new-todo owner :parent-id (:id parent-todo)))
             } "Add")
       )
      )))

(defn unknown-route-view [data owner]
  (reify
      om/IRender
    (render [this]
      (dom/h1 nil "Unknown route")
      )))

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

(defn get-curr-main-view [route-path logged-in?]
  (cond
    (= route-path (rts/home-path))
    (if logged-in?
      home-view
      signup-view)
    (= route-path (rts/login-path))
    login-view
    (= route-path (rts/new-todo-path))
    new-todo-view
    (= route-path (rts/view-todo-path))
    view-todo-view
    (= route-path (rts/re-login-path))
    re-login-view
    :else
    unknown-route-view
    ))

;; note this is currently ignoring route params
(defn get-main-view-state [data]
  (let [route-path (:path (:route data))
        curr-view (get-curr-main-view route-path (:username data))]
    {:path route-path
     :view (om/build curr-view (dissoc data :route))}))

(defn main-view [data owner]
  (reify
      om/IInitState
    (init-state [_]
      (get-main-view-state data))
    om/IWillReceiveProps
    (will-receive-props [this next-props]

      (println "main view recv props")

      (if (not (= (:path (om/get-render-state owner))
                  (:path (:route next-props))))
        (om/set-state! owner (get-main-view-state next-props))))

    om/IWillUpdate
    (will-update [this next-props next-state]

      (println "main view update")

      ;; (println next-state)

      )
    om/IRenderState
    (render-state [this {:keys [path view]}]

      (:view (get-main-view-state data))

      ;; view

      )))

(defn load-main []
  (om/root
   main-view
   st/app-state
   {:target (. js/document (getElementById "app-main"))})
)

(defn load []
  (load-nav)
  (load-main)
  (println "implementing components!!"))
