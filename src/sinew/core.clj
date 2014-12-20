(ns sinew.core
  (:gen-class)
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]
            [sinew.scan-page :as sp]
            [sinew.scan-filenames :as sf]))

(def postgres-db {:subprotocol "postgresql"
                  :subname "//localhost/amoe"
                  :user "amoe"
                  :password "clojure_test"})

(defn ack [path]
  (doseq [name (take 3 (sf/scan-files path))]
    (println (:new name))
    (prn (sp/extract-tags (sp/get-page (:new name))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn meaning-of-life []
  42)

(def meaning2 42)

(defn make-sql-date [year month day]
  (java.sql.Date.
   (.getTimeInMillis
    (java.util.GregorianCalendar. year month day))))

; expects date as string
(defn insert-scene [date]
  (j/insert! postgres-db :scene {:release_date (c/to-sql-date date)}))
             
  
