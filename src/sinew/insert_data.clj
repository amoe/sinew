(ns sinew.insert-data
  (:require [clojure.java.jdbc :as j]
            [clj-time.coerce :as c]
            [sinew.data-service :as data]
            [clojure.pprint :as pprint]))

(declare insert-tags)

(defn read-data-file [path]
  (let [data (read-string (slurp path))]
    (let [tag-map (insert-tags path)]
      (doseq [datum data]
        (pprint/pprint datum)
        (let [scene-id (data/insert-scene nil (:name datum) (:filename datum)
                                          (:description datum))]
          (doseq [tag (:tags datum)]
            (data/insert-scene-tag scene-id (get tag-map tag))))))))

(defn insert-tags [path]
  (let [data (read-string (slurp path))]
    (let [tag-set (set (mapcat identity (map :tags data)))]
      (loop [remaining-tags (seq tag-set)
             generated-ids {}]
        (if (empty? remaining-tags)
          generated-ids
          (let [tag (first remaining-tags)]
            (let [tag-id (data/insert-or-return-tag tag)]
              (recur (rest remaining-tags)
                     (assoc generated-ids tag tag-id)))))))))


            
