(ns sinew.scan-page
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [sinew.configuration :as configuration]))

(defn get-page [type model]
  (let [resolve-url (get (configuration/get-prefixes) type)]
    (if-not resolve-url
      (throw (Exception. (str "unknown scene type: " type))))
    (try
      (html/html-resource
       (java.net.URL. (str resolve-url model)))
      (catch java.io.FileNotFoundException e
        (throw (ex-info "Scene not found" {:type :scene-not-found}))))))
  
(defn convert-page [path]
  (html/html-resource (io/input-stream path)))

(defn clean-description [desc]
  (string/trim (apply str (filter #(not (= % \newline)) desc))))

(defn extract-description [resource]
  (clean-description
   (html/text (first (html/select resource
               #{[:p.story]})))))

(defn extract-tags [resource]
  (distinct
   (map html/text (html/select resource #{[:div.updatetags :a]}))))

  
