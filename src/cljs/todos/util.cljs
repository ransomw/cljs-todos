(ns todos.util
  (:require
   [cljs-time.core :as t]
   [cljs-time.format :as tf]
   [cljs-time.coerce :as tc]
   [cljs-time.extend]
   )
  ;; (:import [goog.date DateTime])
  )

(def date-input-formatter (tf/formatters :year-month-day))
(def date-display-formatter
   (tf/formatters :year-month-day))

(defn date-str? [str]
  (try
    (if (tf/parse date-input-formatter str)
      true)
    (catch :default e
      false))
  )

(defn date-display-str [date-str]

  date-str

  ;;;;; todo: pretty printing
  ;;;;; __ days ago, yesterday, today, tomorrow, Monday, October 30th...

  ;; (let [date (tc/to-local-date (tf/parse date-input-formatter date-str))]
  ;;   (js/console.log date)

  ;;   (cond
  ;;     (t/= (t/today) date)
  ;;     "today"
  ;;     :else
  ;;     (tf/unparse (tf/formatters :year-month-day)
  ;;                 (tc/to-date-time date)
  ;;     )
  ;;   )

  )
