(ns sinew.data-service
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]))

(def postgres-db {:subprotocol "postgresql"
                  :subname "//vlinder/amoe"
                  :user "amoe"
                  :password "clojure_test"})

(declare insert-tag)

(defn update-name [plaintext-name new-name]
  (j/update! postgres-db
             :scene
             {:filename new-name}
             ["plaintext_name = ?" plaintext-name]))
             

(defn query-by-tag
  [tag]
  (j/query postgres-db
           ["SELECT DISTINCT s.filename, s.plaintext_name FROM scene s
             INNER JOIN scene_tags st ON st.scene_id = s.id
             INNER JOIN tag t ON st.tag_id  = t.id
             WHERE t.name = ?" tag]))

(defn insert-scene-tag [scene-id tag-id]
  (:id (first (j/insert! postgres-db :scene_tags {:scene_id scene-id
                                                  :tag_id tag-id}))))

(defn insert-or-return-tag
  [tag]
  (let [result (j/query postgres-db
                        ["SELECT t.id FROM tag t WHERE t.name = ?" tag])]
    (if (empty? result)
      (insert-tag tag)
      (:id (first result)))))


(defn insert-tag [name]
  (:id (first (j/insert! postgres-db :tag {:name name}))))

(defn make-sql-date [year month day]
  (java.sql.Date.
   (.getTimeInMillis
    (java.util.GregorianCalendar. year month day))))

; expects date as string
(defn insert-scene [date plaintext-name filename description]
  (:id (first (j/insert! postgres-db
             :scene
             {:release_date (c/to-sql-date date)
              :plaintext_name plaintext-name
              :filename filename
              :description description}))))
  
(defn toggle-watched
  [plaintext-name]
  (let [updated (j/update! postgres-db
                           :scene
                           {:watched true}
                           ; For some reason you have to do this hack
                           ; in order to supply booleans.
                           ["plaintext_name = ?" plaintext-name])]
    (when (zero? (first updated))
      (throw (Exception. "unable to find scene")))))
