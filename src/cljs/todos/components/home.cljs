(ns todos.components.home
  (:require
   [clojure.string :as str]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [todos.dom-helpers :as domh]
   [todos.component-helpers :as comh]
   [todos.state :as st]
   [todos.routes :as rts]
   [todos.util :as util]
   )
  (:use
   [todos.components.todo-list :only [make-todo-list-view]]
   ))


(defn home-view [data owner]
  (reify
      om/IRender
    (render [this]
      ((domh/center-div :out-cols "two" :in-cols "eight")
       (om/build (make-todo-list-view) (:todos data))
       )
      )))
