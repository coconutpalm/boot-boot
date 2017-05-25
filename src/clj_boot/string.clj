(ns clj-boot.string
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [clj-foundation.fn-spec :refer [=>]]))


(^:private def delimeters [\' \"])
(^:private def delimeter-set (set delimeters))

(s/def ::word-vector     (s/coll-of string?))
(s/def ::maybe-delimeter #(or (delimeter-set %)
                              (nil? %)))
(s/def ::merge-result    (s/tuple ::word-vector ::maybe-delimeter string?))


(=> merge-strings [::word-vector ::maybe-delimeter string? string?] ::merge-result
  "Given a vector of strings, merge strings beginning/ending with quotes into
  a single string and return a vector standalone words and quoted strings.
  Nested / unbalanced quotes will return undefined results."
  [[result delimeter merging] next]

  (let [start (first (seq next))
        end   (last (seq next))]
    (cond
      (nil? delimeter)               [(conj result next) nil ""]
      (and ((set delimeters) start)
           ((set delimeters) end))   [(conj result next) nil ""]
      ((set delimeters) start)       [result start next]
      ((set delimeters) end)         [(conj result (str merging " " next)) nil ""]
      :else                          [result delimeter (str merging " " next)])))



(=> delimited-words [string?] ::word-vector
  "Split a string into words, respecting single or double quoted substrings.
  Nested quotes are not supported.  Unbalenced quotes will return undefined
  results."
  [s]
  (let [words (str/split s #"\s")
        delimeted-word-machine (reduce merge-strings [[] nil ""] words)
        merged-strings (first delimeted-word-machine)
        remainder (last delimeted-word-machine)]
    (if (empty? remainder)
      merged-strings
      (conj merged-strings remainder))))
