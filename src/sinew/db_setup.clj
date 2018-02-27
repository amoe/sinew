(ns sinew.db-setup
  (:require [sinew.configuration :as configuration]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))


;; Ragtime boilerplate for leiningen use

(defn load-config [configuration]
  {:datastore  (jdbc/sql-database (configuration/get-db-spec configuration))
   :migrations (jdbc/load-resources "migrations")})


(defn migrate []
  (repl/migrate (load-config (configuration/new-file-configuration))))

(defn rollback []
  (repl/rollback (load-config (configuration/new-file-configuration))))
