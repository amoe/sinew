(ns sinew.core
  (:gen-class)
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]
            [sinew.jsoup-scanner :as sp]
            [sinew.scan-filenames :as sf]))

(defn ack [path]
  (doseq [name (take 3 (sf/scan-files path))]
    (println (:new name))
    (prn (sp/extract-tags (sp/get-page (:new name))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
