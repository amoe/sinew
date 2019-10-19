(ns sinew.scan-page
  (:require [net.cgrand.enlive-html :as html :refer [attr=]]
            [clojure.java.io :as io]
            [clj-http.client :as client]
            [clojure.string :as string]
            [sinew.utility :as utility]
            [sinew.configuration :as configuration]))

(def archive-stuff 
  {:prefixes
   {:archive {:url "https://archive.org/details/"
              :selectors {:description [:div#descript]
                          :tags [(attr= :itemprop "keywords") :a]}}}})

(defn get-html-resource-input [resolve-url model]
  (-> (str resolve-url model)
      (client/get)
      :body
      utility/string->stream))

(defn get-page [prefixes type model]
  (let [resolve-url (get-in prefixes [type :url])]
    (if-not resolve-url
      (throw (Exception. (str "unknown scene type: " type))))
    (try
      (html/html-resource (get-html-resource-input resolve-url model))
      (catch java.io.FileNotFoundException e
        (throw (ex-info "Scene not found" {:type :scene-not-found}))))))

(defn cleanup-description [desc]
  (string/trim (apply str (filter #(not (= % \newline)) desc))))

;; The appropriate enlive selector should actually be pulled in from the config
;; file based on the prefix.
(defn extract-description [page description-selector]
  (-> page
      (html/select description-selector)
      first
      html/text))

(defn get-description [page description-selector]
  (-> page
      (extract-description description-selector)
      cleanup-description))

(defn extract-tags [resource tag-selector]
  (distinct
   (map html/text (html/select resource #{tag-selector}))))

  
