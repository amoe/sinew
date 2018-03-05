(ns sinew.add-to-db
  (:require [clojure.pprint :as pprint]
            [sinew.scan-page :as scan-page]
            [clojure.tools.logging :refer [fatalf]]
            [taoensso.truss :refer :all]
            [sinew.filesystem-tools :as filesystem-tools]
            [sinew.file-renamer]
            [sinew.system :as system]
            [sinew.data-service :as data]
            [clojure.core.match :refer [match]]
            [sinew.configuration :as configuration]
            [clojure.tools.cli :as cli])
  (:gen-class :main true))

(declare insert-scene
         insert-all-tags
         retrieve-scene-info)

(def cli-options
  [["-F" "--force-scene" "Force addition even if scene not found"]
   ["-f" "--force" "Overwrite existing destination files"]
   ["-d" "--description DESC" "Provide description" :default ""]])

(defn -main
  [& args]
  (let [system (system/build-system)
        parsed (cli/parse-opts args cli-options)]
    (match (:arguments parsed)
      [filename plaintext-name scene-type]
      (let [scene-info (retrieve-scene-info (configuration/get-prefixes (:configuration system))
                                            plaintext-name
                                            (keyword scene-type)
                                            (:options parsed))]
          (insert-scene system
                        filename
                        plaintext-name
                        (:description scene-info)
                        (:tags scene-info)
                        scene-type
                        (:force (:options parsed))))
      :else
      (do 
        (fatalf "usage: FILENAME PLAINTEXT-NAME SCENE-TYPE")
        (System/exit 1)))))

(defn get-description-selector [prefixes scene-type]
  (have! keyword? scene-type)
  (-> prefixes (get scene-type) :selectors :description))

(defn get-tags-selector [prefixes scene-type]
  (have! keyword? scene-type)
  (-> prefixes (get scene-type) :selectors :tags))
 
(defn retrieve-scene-info
  [prefixes plaintext-name scene-type opts]
  (have! keyword? scene-type)
  (prn opts)
  (try
    (let [page (scan-page/get-page prefixes
                                   (keyword scene-type)
                                   plaintext-name)]
      (let [description (scan-page/extract-description page
                                                       (get-description-selector prefixes scene-type))
            tags (scan-page/extract-tags page (get-tags-selector prefixes scene-type))]
        (when (empty? description)
          (throw (ex-info "Empty description, perhaps the scan has failed"
                          {:cause :null-description-after-scan})))

        {:description description :tags tags}))
    (catch clojure.lang.ExceptionInfo e
      (if (:force-scene opts)
        {:description (:description opts) :tags []}
        (throw e)))))

(defn insert-scene
  [{configuration :configuration
    repository :repository} filename plaintext-name description tags scene-type force?]
  (prn description)
  (prn tags)

  (println (str "Will force: " force?))

  (let [extension (sinew.file-renamer/get-extension filename)]
    (let [new-name (str (configuration/get-file-root configuration)
                        "/"
                        scene-type
                        "/"
                        plaintext-name
                        "."
                        extension)]

      (filesystem-tools/mkdir-parents! new-name)
      (println (str "Moving to file: " new-name))
      (sinew.file-renamer/move-file filename new-name force?)
      (let [scene-id (data/insert-scene repository
                                        nil   ; ???
                                         plaintext-name
                                         (str plaintext-name "." extension)
                                         description
                                         scene-type)]
        (insert-all-tags repository scene-id tags)))))
    
(defn insert-all-tags
  [repository scene-id tags]
  (doseq [tag tags]
    (data/insert-scene-tag repository
                           scene-id
                           (data/insert-or-return-tag repository tag))))
                             

