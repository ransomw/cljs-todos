(ns todos.dom-helpers
  (:require
   [om.dom :as dom :include-macros true]
   [clojure.string :as str]
   ))

(defn input [label type ref]
  (dom/div
   nil
   (dom/label nil label)
   (dom/input
    #js {:type type :ref ref})
   )
  )

(defn center-div [& {:keys [out-cols in-cols]}]
  (let [out-cols (if out-cols out-cols "three")
        in-cols (if in-cols in-cols "six")]
  (fn [& elems]
    (dom/section
     nil
     (dom/div
      #js {:className "row"}
      (dom/div
       (clj->js {:className (str/join [out-cols " columns"])})
       "\u00a0") ;; &nbsp
      (dom/div
       (clj->js {:className (str/join [in-cols " columns"])})
       (apply dom/div nil elems)
       (dom/div
       (clj->js {:className (str/join [out-cols " columns"])})
        "\u00a0")
       ))))
  ))

(defn li-link [href text & {:keys [a-class]}]
  (let [attrs (if a-class
                {:href href :className a-class}
                {:href href})]
    (dom/li
     nil
     (dom/a
      (clj->js attrs)
      text))
    )
  )

(defn checkbox [checked on-click]
  (dom/input
   (clj->js {:type "checkbox"
             :checked checked
             :style {
                     :marginBottom "0" ;; override skeleton.css
                     }
             :onClick on-click
             })))
