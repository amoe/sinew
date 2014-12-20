(ns sinew.scan-page
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn convert-page [path]
  (html/html-resource (io/input-stream path)))

(defn clean-description [desc]
  (string/trim (apply str (filter #(not (= % \newline)) desc))))

(defn extract-description [path]
  (clean-description
   (html/text (first (html/select (convert-page path)
               #{[:p.story]})))))

  
