(ns sinew.utility
  (:require [net.cgrand.enlive-html :as enlive-html]))

(defn string->stream
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

