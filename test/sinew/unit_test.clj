(ns sinew.unit-test
  (:require [clojure.test :refer :all]
            [sinew.server :as server]
            [sinew.scan-filenames :as scan-filenames]))


(deftest pick-next-scene-works
  (let [configuration nil
        repository nil
        watched nil]
    (let [result (server/pick-next-scene configuration repository watched)]
      (is result))))

(deftest lowercase-works 
  (let [result (scan-filenames/lowercase "Hello world")]
    (is (= "hello world" result))))
