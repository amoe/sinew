(ns sinew.scan-page
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn get-page [model]
  (html/html-resource
   (java.net.URL. (str "b1" model))))

(defn convert-page [path]
  (html/html-resource (io/input-stream path)))

(defn clean-description [desc]
  (string/trim (apply str (filter #(not (= % \newline)) desc))))

(defn extract-description [resource]
  (clean-description
   (html/text (first (html/select resource
               #{[:p.story]})))))

(defn extract-tags [resource]
  (map html/text (html/select resource #{[:div.updatetags :a]})))

  
