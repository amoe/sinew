(ns sinew.configuration)

(defprotocol Configuration
  (get-db-spec [this]))

(defrecord FileConfiguration []
  Configuration
  (get-db-spec [this]
    (-> "/usr/local/etc/sinew.edn" slurp read-string :db-spec)))

(defn new-file-configuration []
  (->FileConfiguration))

(defn get-file-root []
  (-> "/usr/local/etc/sinew.edn" slurp read-string :file-root))

(defn get-prefixes []
  (-> "/usr/local/etc/sinew.edn" slurp read-string :prefixes))
