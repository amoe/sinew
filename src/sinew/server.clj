(ns sinew.server
  (:require [ring.adapter.jetty :as jetty]
            [clojure.pprint :as pprint]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [sinew.select-by-tag :as select-by-tag]))

(defn render-index []
  {:headers {"Content-Type" "text/plain"}
   :body (with-out-str
           (pprint/pprint (select-by-tag/query-by-tag "some-tag")))})

(defroutes app
  (GET "/" [] (render-index))
  (GET "/user/:id" [id]
       (str "<h1>" id "</h1>"))
  (route/not-found "<h1>Page not found</h1>"))


(defn run []
  (jetty/run-jetty #'app {:port 8000 :join? false}))
            
