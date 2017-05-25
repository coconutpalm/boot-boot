(ns clj-boot.string-test
  (:require [clojure.test :refer :all]
            [clj-boot.string :refer :all]))


(testing "merge-strings"
  (testing "When not merging, delimiter is nil and words are added to result"
    (is (= [["The" "blind"] nil ""]
           (-> [[] nil ""]
              (merge-strings "The")
              (merge-strings "blind")))))

  (testing "Delimiter characters"
    (testing "A word beginning and ending with a delimiter char is added to result"
      (is (= [["The" "'blind'"] nil ""]
             (-> [[] nil ""]
                (merge-strings "The")
                (merge-strings "'blind'")))))

    (testing "After a start delimiter, the delimiter is noted and words are added to 'merging'"
      (is (= [["The" "'blind'" "man" "said"] \' "'I see"])
          (-> [[] nil ""]
             (merge-strings "The")
             (merge-strings "'blind'")
             (merge-strings "man")
             (merge-strings "said")
             (merge-strings "'I")
             (merge-strings "see"))))

    (testing "On end delimiter, merged words are added to result and 'merging' state is cleared."
      (is (= [["The" "'blind'" "man" "said" "'I see you.'"] nil ""])
          (-> [[] nil ""]
             (merge-strings "The")
             (merge-strings "'blind'")
             (merge-strings "man")
             (merge-strings "said")
             (merge-strings "'I")
             (merge-strings "see")
             (merge-strings "you.'"))))))

(testing "delimited-words")
