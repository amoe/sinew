(ns sinew.add-to-db
  (:require [clojure.pprint :as pprint]
            [sinew.scan-page]
            [sinew.file-renamer]
            [sinew.data-service :as data]
            [clojure.core.match :refer [match]]
            [clojure.tools.cli :as cli])
  (:gen-class :main true))

(declare insert-scene
         insert-all-tags
         retrieve-scene-info)

(def prefix "/mnt/mydrive/videos")

(def cli-options
  [["-F" "--force-scene" "Force addition even if scene not found"]
   ["-f" "--force" "Overwrite existing destination files"]
   ["-d" "--description DESC" "Provide description" :default ""]])

(defn -main
  [& args]
  (let [parsed (cli/parse-opts args cli-options)]
    (match (:arguments parsed)
      [filename plaintext-name scene-type]
      (let [scene-info (retrieve-scene-info plaintext-name scene-type
                                            (:options parsed))]
          (insert-scene filename
                        plaintext-name
                        (:description scene-info)
                        (:tags scene-info)
                        scene-type
                        (:force (:options parsed))))
      :else
      (throw (Exception. (str "usage: FILENAME PLAINTEXT-NAME SCENE-TYPE"))))))

(defn retrieve-scene-info
  [plaintext-name scene-type opts]
  (prn opts)
  (try
    (let [page (sinew.scan-page/get-page (keyword scene-type)
                                         plaintext-name)]
      (let [description (sinew.scan-page/extract-description page)
            tags (sinew.scan-page/extract-tags page)]
        {:description description :tags tags}))
    (catch clojure.lang.ExceptionInfo e
      (if (:force-scene opts)
        {:description (:description opts) :tags []}
        (throw e)))))


(defn insert-scene
  [filename plaintext-name description tags scene-type force?]
  (prn description)
  (prn tags)

  (println (str "Will force: " force?))

  (let [extension (sinew.file-renamer/get-extension filename)]
    (let [new-name (str prefix
                        "/"
                        scene-type
                        "/"
                        plaintext-name
                        "."
                        extension)]
      (sinew.file-renamer/move-file filename new-name force?)
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
                             

