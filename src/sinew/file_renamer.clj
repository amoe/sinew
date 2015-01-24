(ns sinew.file-renamer
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]
            [sinew.data-service :as data]))

(import '(java.io File)
        '(org.apache.commons.io FilenameUtils))

(declare rename-all-files
         rename-file
         update-name)

(defn -main
  [& args]
  (rename-all-files (first args) (second args)))

(defn rename-all-files [data-path prefix]
  (let [data (read-string (slurp data-path))]
    (doseq [datum data]
      (let [extension (FilenameUtils/getExtension (:filename datum))]
        (let [new-name (str prefix "/" (:name datum) "." extension)]
          (rename-file (str prefix "/" (:filename datum)) new-name)
          (data/update-name
           (:name datum) (str (:name datum) "." extension)))))))

(defn get-extension
  [path]
  (FilenameUtils/getExtension path))
  

(defn rename-file [old new]
  (when (not (= old new))
    (let [result (.renameTo (File. old) (File. new))]
      (when (not result)        
        (throw (Exception. (str "failed to rename file: " old)))))))


