(ns sinew.scan-filenames
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [me.raynes.fs :as fs]
            [sinew.scan-page]))

(declare scan-files
         scan-filename
         substitute-whitespace
         lowercase
         substitute-underscore
         remove-extension
         remove-resolution
         remove-suffix
         remove-leftover-trailing-dashes
         remove-leftover-leading-dashes
         collapse-dashes
         remove-number)

; Two top level functions
; SCAN-AND-PRINT scans a list of files and fixes them.
; In the meantime the user modifies the data file in a text editor
; READ-MODIFIED-LIST reads in the list and gets the changes.

(defn read-modified-list [path type]
  (let [data (read-string (slurp path))]
    (doseq [rec data]
       (try
         (let [page (sinew.scan-page/get-page type (:new rec))]
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
  (pprint/pprint (scan-files path)))

(defn scan-directory [dir-path]
  (doall (map (fn [x] {:original (str x) :new (scan-filename (fs/base-name x))})
              (file-seq (io/file dir-path)))))

(defn scan-files [path]
  (with-open [rdr (io/reader path)]
    (doall (map (fn [x] {:original x :new (scan-filename (fs/base-name x))})  (line-seq rdr)))))

(defn scan-filename [filename]
  (-> filename
      lowercase
      substitute-whitespace
      substitute-underscore
      remove-extension
      remove-resolution
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
