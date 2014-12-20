(ns sinew.scan-filenames
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [sinew.scan-page]))

(declare scan-files
         scan-filename
         substitute-whitespace
         lowercase
         substitute-underscore
         remove-extension
         remove-resolution
         remove-prefix
         remove-sitename
         remove-suffix
         remove-leftover-trailing-dashes
         remove-leftover-leading-dashes
         collapse-dashes
         remove-number)

(defn read-modified-list [path]
  (let [data (read-string (slurp path))]
    (doseq [rec data]
       (try
         (let [page (sinew.scan-page/get-page (:new rec))]
           (let [desc (sinew.scan-page/extract-description page)
                 tags (sinew.scan-page/extract-tags page)]
             (let [outrec {:filename (:original rec)
                           :name (:new rec)
                           :description desc
                           :tags tags}]
               (spit "out.clj"  (str (pr-str outrec) "\n")  :append true))))
         (catch Exception e
           (println (str "Unable to get description for " (:new rec))))))))

(defn scan-and-print [path]
  (doseq [name (scan-files path)]
    (prn name)))

(defn scan-files [path]
  (with-open [rdr (io/reader path)]
    (doall (map (fn [x] {:original x :new (scan-filename x)})  (line-seq rdr)))))

(defn scan-filename [filename]
  (-> filename
      lowercase
      substitute-whitespace
      substitute-underscore
            remove-sitename

      remove-extension
      remove-resolution
      remove-prefix
      remove-suffix
      remove-leftover-trailing-dashes
      remove-leftover-leading-dashes
            remove-number
      remove-leftover-leading-dashes
      collapse-dashes))


(defn lowercase [str]
  (string/lower-case str))

(defn substitute-whitespace [str]
  (string/replace str " " "-"))

(defn substitute-underscore [str]
  (string/replace str "_" "-"))

(defn remove-extension [str]
  (string/replace str #"\..*?$" ""))

(defn remove-resolution [str]
  (string/replace str #"\d{3,4}x\d{3,4}" ""))

(defn remove-prefix [str]
  (string/replace str #"^fa-" ""))

(defn remove-sitename [str]
  (string/replace str #"sitename(.com)?" ""))

(defn remove-suffix [str]
  (string/replace str #"01$" ""))

(defn remove-leftover-trailing-dashes [str]
  (string/replace str #"-*$" ""))

(defn remove-leftover-leading-dashes [str]
  (string/replace str #"^-*" ""))

(defn collapse-dashes [str]
  (string/replace str #"--*" "-"))

(defn remove-number [str]
  (string/replace str #"^\d+" ""))
