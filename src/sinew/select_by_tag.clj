(ns sinew.select-by-tag
  (:gen-class)
  (:require [sinew.data-service :as data]))

(declare query-by-tag)

(defn -main
  [& args]
  ; FIXME nil should be an instance of repository
  (doseq [result (map :filename (data/query-by-tag nil (first args)))]
    (println  result)))
