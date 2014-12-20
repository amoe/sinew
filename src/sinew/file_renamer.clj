(ns sinew.file-renamer
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]))


(import '(java.io File))

(defn rename-all-files [data-path]
  (let [data (read-string (slurp data-path))]
    (doseq [datum data]
      (.renameTo (.File (:filename datum)) (.File (:name datum))))))
