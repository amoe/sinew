(ns sinew.server-test
  (:require [clojure.test :refer :all]
            [sinew.server :as server]))

(deftest sanity
  (is (= 4 (+ 2 2))))
