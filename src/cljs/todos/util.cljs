(ns todos.util
  (:require
   [clojure.string :as str]
   [cljs-time.core :as t]
   [cljs-time.format :as tf]
   [cljs-time.coerce :as tc]
   [cljs-time.extend]
   )
  )

(def date-input-formatter (tf/formatters :year-month-day))

(defn date-str? [str]
  (try
    (if (tf/parse date-input-formatter str)
      true)
    (catch :default e
      false)))

(defn date-str-leq [date-str-lt date-str-gt]
  (let [parse-date
        (fn [date-str]
          (tc/to-local-date (tf/parse date-input-formatter date-str)))
        date-lt (parse-date date-str-lt)
        date-gt (parse-date date-str-gt)]
    (t/before? date-lt date-gt)))

(defn date-display-str [date-str]
  (if date-str
    (let [date-obj (tc/to-local-date
                    (tf/parse date-input-formatter date-str))
          today (tc/to-local-date (t/today))]
      (cond
        (t/before? date-obj today)
        "overdue!"
        (t/= date-obj today)
        "today"
        (> 7 (t/in-days (t/interval today date-obj)))
        (get (str/split (tf/unparse (tf/formatters :rfc822)
                                    (tc/to-date-time date-obj))
                        ",") 0)
        :else
        (tf/unparse (tf/formatters :year-month-day)
                    (tc/to-date-time date-obj))
        ))))
