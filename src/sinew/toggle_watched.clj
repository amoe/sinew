(ns sinew.toggle-watched
  (:require [sinew.data-service :as data]
            [sinew.system :as system]))

(defn -main
  [& args]
  (let [system (system/build-system)]
    (data/toggle-watched (:repository system) (first args))))


