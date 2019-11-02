(ns sinew.jsoup-scanner
  (:require [sinew.utility :as utility]
            [clojure.string :as string]
            [clj-http.client :as client])
  (:import [org.jsoup Jsoup]))

  (def foo "dd[itemprop=keywords] a")

(defn get-html-from-string [string]
  (Jsoup/parse string))

(defn get-html-from-url [resolve-url model]
  (-> (str resolve-url model)
      (client/get)
      :body
      Jsoup/parse))

;; Public functions

(defn get-page [prefixes type model]
  (let [resolve-url (get-in prefixes [type :url])]
    (if-not resolve-url
      (throw (Exception. (str "unknown scene type: " type))))
    (try
      (get-html-from-url resolve-url model)
      (catch java.io.FileNotFoundException e
        (throw (ex-info "Scene not found" {:type :scene-not-found}))))))

(defn cleanup-description [desc]
  (string/trim (apply str (filter #(not (= % \newline)) desc))))

(defn extract-description [page description-selector]
  (-> page 
      (.select description-selector)
      (.text)
      (cleanup-description)))

(defn extract-tags [page tag-selector]
  (->> (.select page tag-selector)
       (map (fn [x] (.text x)))
       (distinct)))

      

