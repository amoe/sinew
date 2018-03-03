(ns sinew.filesystem-tools
  (:require [me.raynes.fs :as fs]))

;; 'path' should be the pathname to a file, not a dir.  fs/parent is equivalent
;; to dirname(1).
(defn mkdir-parents! [path]
  (fs/mkdirs (fs/parent path)))
