(ns sinew.configuration)

(defprotocol Configuration
  (get-db-spec [this])
  (get-file-root [this])
  (get-prefixes [this]))

(defrecord FileConfiguration []
  Configuration
  (get-db-spec [this]
    (-> "/usr/local/etc/sinew.edn" slurp read-string :db-spec))
  (get-file-root [this]
    (-> "/usr/local/etc/sinew.edn" slurp read-string :file-root))
  (get-prefixes [this]
    (-> "/usr/local/etc/sinew.edn" slurp read-string :prefixes)))

(defn new-file-configuration []
  (->FileConfiguration))

