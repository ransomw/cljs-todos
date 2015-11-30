(ns todos.components.config
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   ))

(defn config-view [data owner]
  (reify
      om/IRender
    (render [this]
      ((domh/center-div :out-cols "two" :in-cols "eight")
       (dom/h3 nil "config")
      ))))
