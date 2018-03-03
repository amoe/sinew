(ns sinew.data-service
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]
            [sinew.configuration :as configuration]))


(defn make-sql-date [year month day]
  (java.sql.Date.
   (.getTimeInMillis
    (java.util.GregorianCalendar. year month day))))

(defprotocol Repository
  (insert-tag [this name])
  (update-name [this plaintext-name new-name])
  (list-all-scenes [this])
  (query-by-tag [this tag])
  (insert-scene-tag [this scene-id tag-id])
  (insert-or-return-tag [this tag])
  (toggle-watched [this plaintext-name])
  (set-rating [this plaintext-name rating])
  (get-tag-data-from-db [this])
  (insert-scene [this date plaintext-name filename description scene_type])
  (get-scenes-with-tags [this]))


;; If any method here doesn't refer to postgres-db, then it's wrong and should
;; be factored out.
(defrecord PostgresqlRepository [postgres-db]
  Repository
  (insert-tag [this name]
    (:id (first (j/insert! postgres-db :tag {:name name}))))
  (update-name [this plaintext-name new-name]
    (j/update! postgres-db
               :scene
               {:filename new-name}
               ["plaintext_name = ?" plaintext-name]))
  (list-all-scenes [this]
    (j/query postgres-db
             ["SELECT s.id, s.release_date, s.plaintext_name, s.filename,
                    s.description, s.watched, s.scene_type, s.rating
             FROM scene s"]))
  (query-by-tag [this tag]
    (j/query postgres-db
             ["SELECT s.filename, s.plaintext_name, s.watched, s.scene_type,
                    s.description, s.rating
             FROM scene s
             INNER JOIN scene_tags st ON st.scene_id = s.id
             INNER JOIN tag t ON st.tag_id  = t.id
             WHERE t.name = ?
             ORDER BY s.plaintext_name" tag]))
  (insert-scene-tag [this scene-id tag-id]
    (:id (first (j/insert! postgres-db :scene_tags {:scene_id scene-id
                                                    :tag_id tag-id}))))
  (insert-or-return-tag [this tag]
    (let [result (j/query postgres-db
                          ["SELECT t.id FROM tag t WHERE t.name = ?" tag])]
      (if (empty? result)
        (insert-tag this tag)
        (:id (first result)))))
  (toggle-watched [this plaintext-name]
    (let [updated (j/update! postgres-db
                             :scene
                             {:watched true}
                                        ; For some reason you have to do this hack
                                        ; in order to supply booleans.
                             ["plaintext_name = ?" plaintext-name])]
      (when (zero? (first updated))
        (throw (Exception. "unable to find scene")))))
  (set-rating [this plaintext-name rating]
    (let [updated (j/update! postgres-db
                             :scene
                             {:rating rating}
                             ["plaintext_name = ?" plaintext-name])]
      (when (zero? (first updated))
        (throw (Exception. "unable to find scene")))))
  (get-tag-data-from-db [this]
    (j/query postgres-db
             ["SELECT s.filename, t.name FROM scene s
             INNER JOIN scene_tags st ON s.id = st.scene_id
             INNER JOIN tag t ON st.tag_id = t.id
             "]))
  (insert-scene [this date plaintext-name filename description scene_type]
    (:id (first (j/insert! postgres-db
                           :scene
                           {:release_date (c/to-sql-date date)
                            :plaintext_name plaintext-name
                            :filename filename
                            :watched false
                            :scene_type scene_type
                            :description description}))))
  (get-scenes-with-tags [this]
    (map (fn [x] {:name (first x) :tags (second x)})
         (reduce (fn [acc item]
                   (update-in acc [(:filename item)]
                              (fn [old-value]
                                (conj old-value (:name item)))))
                 {}
                 (get-tag-data-from-db this)))))

(defn new-postgresql-repository [db-spec]
  (->PostgresqlRepository db-spec))
