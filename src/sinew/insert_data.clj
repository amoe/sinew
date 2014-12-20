(ns sinew.insert-data
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]))

(declare insert-scene-tag
         read-data-file
         insert-tags
         insert-scene
         insert-tag)

(def postgres-db {:subprotocol "postgresql"
                  :subname "//localhost/amoe"
                  :user "amoe"
                  :password "clojure_test"})

(defn -main
  [& args]
  (read-data-file (first args)))

(defn read-data-file [path]
  (let [data (read-string (slurp path))]
    (let [tag-map (insert-tags path)]
      (doseq [datum data]
        (let [scene-id (insert-scene nil (:name datum) (:filename datum)
                                     (:description datum))]
          (doseq [tag (:tags datum)]
            (insert-scene-tag scene-id (get tag-map tag))))))))

(defn insert-tags [path]
  (let [data (read-string (slurp path))]
    (let [tag-set (set (mapcat identity (map :tags data)))]
      (loop [remaining-tags (seq tag-set)
             generated-ids {}]
        (if (empty? remaining-tags)
          generated-ids
          (let [tag (first remaining-tags)]
            (let [tag-id (insert-tag tag)]
              (recur (rest remaining-tags)
                     (assoc generated-ids tag tag-id)))))))))


(defn insert-scene-tag [scene-id tag-id]
  (:id (first (j/insert! postgres-db :scene_tags {:scene_id scene-id
                                                  :tag_id tag-id}))))

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
             
  




  
            
