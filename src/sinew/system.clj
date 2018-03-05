(ns sinew.system
  (:require [sinew.configuration :as configuration]
            [sinew.data-service :as data]))

(defn build-system []
  (let [configuration (configuration/new-file-configuration)]
    {:repository (data/new-postgresql-repository (configuration/get-db-spec configuration))
     :configuration configuration}))
