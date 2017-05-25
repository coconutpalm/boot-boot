(ns clj-boot.string
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [clj-foundation.fn-spec :refer [=>]]))


(^:private def delimiters [\' \"])
(^:private def delimiter-set (set delimiters))

(s/def ::word-vector     (s/coll-of string?))
(s/def ::maybe-delimiter #(or (delimiter-set %)
                              (nil? %)))
(s/def ::merge-result    (s/tuple ::word-vector ::maybe-delimiter string?))


(=> merge-strings [::word-vector ::maybe-delimiter string? string?] ::merge-result
  "Given a vector of strings, merge strings beginning/ending with quotes into
  a single string and return a vector standalone words and quoted strings.
  Nested / unbalanced quotes will return undefined results."
  [[result delimiter merging] next]

  (let [start (first (seq next))
        end   (last (seq next))]
    (cond
      (and ((set delimiters) start)
           ((set delimiters) end))   [(conj result next) nil ""]
      ((set delimiters) start)       [result start next]
      ((set delimiters) end)         [(conj result (str merging " " next)) nil ""]
      (nil? delimiter)               [(conj result next) nil ""]
      :else                          [result delimiter (str merging " " next)])))


(=> delimited-words [string?] ::word-vector
  "Split a string into words, respecting single or double quoted substrings.
  Nested quotes are not supported.  Unbalanced quotes will return undefined
  results."
  [s]
  (let [words (str/split s #"\s")
        delimited-word-machine (reduce merge-strings [[] nil ""] words)
        merged-strings (first delimited-word-machine)
        remainder (last delimited-word-machine)
        delimiter (second delimited-word-machine)]
    (if (empty? remainder)
      merged-strings
      (conj merged-strings (str remainder delimiter)))))
