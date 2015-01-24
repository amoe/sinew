(ns sinew.toggle-watched
  (:require [sinew.data-service :as data]))

(defn -main
  [& args]
  (data/toggle-watched (first args)))


