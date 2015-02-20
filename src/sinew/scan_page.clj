(ns sinew.scan-page
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def prefixes
  {:a1 "b1"
   :a2 "b2"
   :a3 "b3"})

(defn get-page [type model]
  (let [resolve-url (type prefixes)]
    (if-not resolve-url
      (throw (Exception. "unknown scene type" type)))
    (html/html-resource
     (java.net.URL. (str (type prefixes) model)))))

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

  
