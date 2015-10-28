(ns todos.dom-helpers
  (:require
   [om.dom :as dom :include-macros true]
   ))


(defn input [label type ref]
  (dom/div
   nil
   (dom/label nil label)
   (dom/input
    #js {:type type :ref ref})
   )
  )

(defn center-div [& elems]
  (dom/section
   nil
   (dom/div
    #js {:className "row"}
    (dom/div
     #js {:className "three columns"}
     "\u00a0") ;; &nbsp
    (dom/div
     #js {:className "six columns"}
     (apply dom/div nil elems)
     (dom/div
      #js {:className "three columns"}
      "\u00a0")
     ))))


;; todo: don't add null class name when called w/o options
(defn li-link [href text & opts]
  (let [classes (first opts)
        attrs (if opts
                {:href href :className (:a classes)}
                {:href href})]
    (dom/li
     nil
     (dom/a
      #js {:href href :className (:a classes)}
      ;; why does om require js literals?
      ;; (js-obj attrs)
      text))
    )
  )
