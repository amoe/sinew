(ns sinew.select-by-tag
  (:gen-class)
  (:require [sinew.data-service :as data]))

(declare query-by-tag)

(defn -main
  [& args]
  (doseq [filename (data/query-by-tag (first args))]
    (println filename)))

