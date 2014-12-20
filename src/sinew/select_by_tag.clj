(ns sinew.select-by-tag
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [sinew.scan-page]
            [sinew.insert-data :as sdata]
            [sinew.file-renamer]
            [clojure.java.jdbc :as j]))

(declare query-by-tag)

(defn -main
  [& args]
  (pprint/pprint (query-by-tag (first args))))

(defn query-by-tag
  [tag]
  (map :filename (j/query sdata/postgres-db
           ["SELECT DISTINCT s.filename FROM scene s
             INNER JOIN scene_tags st ON st.scene_id = s.id
             INNER JOIN tag t ON st.tag_id  = t.id
             WHERE t.name = ?" tag])))
