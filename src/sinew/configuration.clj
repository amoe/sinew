(ns sinew.configuration)

(defprotocol Configuration
  (get-db-spec [this])
  (get-file-root [this]))

(defrecord FileConfiguration []
  Configuration
  (get-db-spec [this]
    (-> "/usr/local/etc/sinew.edn" slurp read-string :db-spec))
  (get-file-root [this]
    (-> "/usr/local/etc/sinew.edn" slurp read-string :file-root)))

(defn new-file-configuration []
  (->FileConfiguration))

(defn get-prefixes []
  (-> "/usr/local/etc/sinew.edn" slurp read-string :prefixes))
