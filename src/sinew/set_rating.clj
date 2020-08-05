(ns sinew.set-rating
  (:require [sinew.data-service :as data]
            [sinew.system :as system]))

;; CLI tool
;; usage: scene-name rating-as-number
(defn -main
  [& args]
  (let [system (system/build-system)
        rating (read-string (second args))]
    (data/set-rating (:repository system)
                     (first args) 
                     rating)))


