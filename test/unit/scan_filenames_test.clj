(ns unit.scan-filenames-test
  (:require [clojure.test :refer :all]
            [sinew.scan-filenames :as sut]))

(deftest lowercase-works 
  (let [result (sut/lowercase "Hello world")]
    (is (= "hello world" result))))
