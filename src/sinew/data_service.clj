(ns sinew.data-service
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))

(def postgres-db {:subprotocol "postgresql"
                  :subname "//localhost/sinew"
                  :user "sinew"
                  :password "bh25VnRDuivzOiIl"})

; Ragtime boilerplate for leiningen use

(defn load-config []
  {:datastore  (jdbc/sql-database postgres-db)
   :migrations (jdbc/load-resources "migrations")})


(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))


(declare insert-tag)

(defn update-name [plaintext-name new-name]
  (j/update! postgres-db
             :scene
             {:filename new-name}
             ["plaintext_name = ?" plaintext-name]))
             

(defn list-all-scenes []
  (j/query postgres-db
           ["SELECT s.id, s.release_date, s.plaintext_name, s.filename,
                    s.description, s.watched, s.scene_type
             FROM scene s"]))
             
             
  

(defn query-by-tag
  [tag]
  (j/query postgres-db
           ["SELECT s.filename, s.plaintext_name, s.watched, s.scene_type,
                    s.description
             FROM scene s
             INNER JOIN scene_tags st ON st.scene_id = s.id
             INNER JOIN tag t ON st.tag_id  = t.id
             WHERE t.name = ?
             ORDER BY s.plaintext_name" tag]))

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

(defn insert-scene [date plaintext-name filename description scene_type]
  (:id (first (j/insert! postgres-db
             :scene
             {:release_date (c/to-sql-date date)
              :plaintext_name plaintext-name
              :filename filename
              :watched false
              :scene_type scene_type
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

(defn set-rating [plaintext-name rating]
  (let [updated (j/update! postgres-db
                           :scene
                           {:rating rating}
                           ["plaintext_name = ?" plaintext-name])]
    (when (zero? (first updated))
      (throw (Exception. "unable to find scene")))))

(defn get-tag-data-from-db []
  (j/query postgres-db
           ["SELECT s.filename, t.name FROM scene s
             INNER JOIN scene_tags st ON s.id = st.scene_id
             INNER JOIN tag t ON st.tag_id = t.id
             "]))


(defn get-scenes-with-tags []
  (map (fn [x] {:name (first x) :tags (second x)})
       (reduce (fn [acc item]
                 (update-in acc [(:filename item)]
                            (fn [old-value]
                              (conj old-value (:name item)))))
               {}
               (get-tag-data-from-db))))


