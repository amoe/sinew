(ns sinew.configuration)

(defn get-file-root []
  (-> "/usr/local/etc/sinew.edn" slurp read-string :file-root))

(defn get-prefixes []
  (-> "/usr/local/etc/sinew.edn" slurp read-string :prefixes))
