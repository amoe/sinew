(ns sinew.system
  (:require [sinew.configuration :as configuration]
            [sinew.data-service :as data]
            [sinew.filesystem :as filesystem]))

(defn build-system []
  (let [configuration (configuration/new-file-configuration)]
    {:repository (data/new-postgresql-repository (configuration/get-db-spec configuration))
     :configuration configuration
     :filesystem (filesystem/new-filesystem)}))
