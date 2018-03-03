(ns sinew.file-renamer
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]
            [sinew.data-service :as data]))

(import '(java.io File)
        '(org.apache.commons.io FileUtils)
        '(org.apache.commons.io FilenameUtils))

(declare rename-all-files
         rename-file
         update-name
         get-path)

(defn -main
  [& args]
  (rename-all-files (first args) (second args)))

(defn rename-all-files [repository data-path prefix]
  (let [data (read-string (slurp data-path))]
    (doseq [datum data]
      (let [extension (FilenameUtils/getExtension (:filename datum))]
        (let [new-name (str prefix "/" (:name datum) "." extension)]
          (rename-file (:filename datum) new-name)
          (data/update-name repository
                            (:name datum)
                            (str (:name datum) "." extension)))))))

(defn get-extension
  [path]
  (FilenameUtils/getExtension path))
  

(defn rename-file [old new]
  (when (not (= old new))
    (println new)
    (FileUtils/moveFile (File. old) (File. new))))
    

(defn move-file [source target overwrite?]
  (let [copy-options (if overwrite?
                       [java.nio.file.StandardCopyOption/REPLACE_EXISTING]
                       [])]
    (java.nio.file.Files/move
     (get-path source) (get-path target)
     (into-array java.nio.file.CopyOption copy-options))))

; Required to make the array call, otherwise clj attempts to call the wrong
; method.
(defn get-path [spec]
  (java.nio.file.Paths/get spec (into-array String [])))
