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

