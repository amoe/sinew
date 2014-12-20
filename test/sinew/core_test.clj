(ns sinew.core-test
  (:require [clojure.test :refer :all]
            [sinew.core :refer :all]
            [clojure.java.jdbc :as j]))


(def postgres-db {:subprotocol "postgresql"
                  :subname "//localhost/amoe"
                  :user "amoe"
                  :password "clojure_test"})

(deftest a-test
  (testing "Test a static assertion"
    (is (= (meaning-of-life) 42))))
