(ns todos.components.home
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   )
  (:use
   [todos.components.todo-list :only [todo-list-view]]
   ))


(defn home-view [data owner]
  (reify
      om/IRender
    (render [this]
      ((domh/center-div :out-cols "two" :in-cols "eight")
       (dom/h1 nil "home")
       )
      )))
