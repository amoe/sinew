(ns sinew.unit-test
  (:require [clojure.test :refer :all]
            [sinew.scan-filenames :as scan-filenames]))

(deftest lowercase-works 
  (let [result (scan-filenames/lowercase "Hello world")]
    (is (= "hello world" result))))
