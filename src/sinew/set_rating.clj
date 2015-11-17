(ns sinew.set-rating
  (:require [sinew.data-service :as data]))

; usage: scene-name rating-as-number
(defn -main
  [& args]
  (data/set-rating (first args) (read-string (second args))))


