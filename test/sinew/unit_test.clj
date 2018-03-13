(ns sinew.unit-test
  (:require [clojure.test :refer :all]
            [sinew.server :as server]
            [sinew.data-service :as data-service]
            [sinew.scan-filenames :as scan-filenames]
            [shrubbery.core :refer [stub]]
            [sinew.configuration :as configuration]))

(deftest pick-next-scene-returns-nil-when-none-left
  (let [configuration (stub configuration/Configuration
                         {:get-file-root "/nonexistent"})
        repository (stub data-service/Repository
                         {:list-all-scenes []})
        watched false]
    (let [result (server/pick-next-scene configuration repository watched)]
      (is (nil? result)))))

(deftest lowercase-works 
  (let [result (scan-filenames/lowercase "Hello world")]
    (is (= "hello world" result))))
