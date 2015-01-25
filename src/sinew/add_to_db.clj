(ns sinew.add-to-db
  (:require [clojure.pprint :as pprint]
            [sinew.scan-page]
            [sinew.file-renamer]
            [sinew.data-service :as data]))

(declare insert-scene
         insert-all-tags)

(def prefix "/mnt/mydrive/videos")

(defn -main
  [& args]
  (let [filename (first args)
        plaintext-name (second args)
        scene-type (third args)]
    (let [page (sinew.scan-page/get-page (keyword scene-type)
                                         plaintext-name)]
      (let [description (sinew.scan-page/extract-description page)
            tags (sinew.scan-page/extract-tags page)]
        (insert-scene filename
                      plaintext-name
                      description
                      tags
                      scene-type)))))


(defn insert-scene
  [filename plaintext-name description tags scene-type]
  (prn description)
  (prn tags)

  (let [extension (sinew.file-renamer/get-extension filename)]
    (let [new-name (str prefix
                        "/"
                        scene-type
                        "/"
                        plaintext-name
                        "."
                        extension)]
      (sinew.file-renamer/rename-file filename new-name)
      (let [scene-id (data/insert-scene nil
                                         plaintext-name
                                         (str plaintext-name "." extension)
                                         description
                                         scene-type)]
        (insert-all-tags scene-id tags)))))
    
(defn insert-all-tags
  [scene-id tags]
  (doseq [tag tags]
    (data/insert-scene-tag scene-id
                           (data/insert-or-return-tag tag))))
                             

