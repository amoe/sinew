(ns sinew.filesystem
  (:require [clojure.java.io :as io]
            [failjure.core :as f]))

(defprotocol Filesystem
  (get-mtime [this path]))

(defn %get-mtime [path]
  (let [result (.lastModified (io/file path))]
    (if (zero? result)
      (f/fail "file does not exist, or has zero mtime: %s" path)
      result)))

(defrecord FilesystemImpl []
  Filesystem
  (get-mtime [this path]
    ;; we should really be using Files.readAttributes() method, which allows
    ;; distinguishing the error cases, but blah.
    (%get-mtime path)))


(defn new-filesystem [] (->FilesystemImpl))
