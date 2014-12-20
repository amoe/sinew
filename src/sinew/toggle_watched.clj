(ns sinew.toggle-watched
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [sinew.scan-page]
            [sinew.insert-data :as sdata]
            [sinew.file-renamer]
            [clojure.java.jdbc :as j]))

(declare toggle-watched)

(defn -main
  [& args]
  (toggle-watched (first args)))


(defn toggle-watched
  [plaintext-name]
  (let [updated (j/update! sdata/postgres-db
                           :scene
                           {:watched true}
                           ; For some reason you have to do this hack
                           ; in order to supply booleans.
                           ["plaintext_name = CAST(? AS INTEGER)"
                            plaintext-name])]
    (when (zero? (first updated))
      (throw (Exception. "unable to find scene")))))
