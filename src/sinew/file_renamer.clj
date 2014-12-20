(ns sinew.file-renamer
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]))

(import '(java.io File)
        '(org.apache.commons.io FilenameUtils))

(declare rename-file)

(def postgres-db {:subprotocol "postgresql"
                  :subname "//localhost/amoe"
                  :user "amoe"
                  :password "clojure_test"})


(defn rename-all-files [data-path]
  (let [data (read-string (slurp data-path))]
    (doseq [datum data]
      (let [extension (FilenameUtils/getExtension (:filename datum))]
        (let [new-name (str (:name datum) "." extension)]
          (rename-file (:filename datum) new-name))))))
;          (update-name (:name datum)


(defn rename-file [old new]
  (let [result (.renameTo (File. old) (File. new))]
    (when (not result)
      (throw (Exception. "failed to rename file")))))


(defn update-name [plaintext-name new-name]
  (j/update! postgres-db
             :scene
             {:filename new-name}
             ["plaintext_name = ?" plaintext-name]))
             
  




