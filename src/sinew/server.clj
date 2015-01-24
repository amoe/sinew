(ns sinew.server
  (:require [ring.adapter.jetty :as jetty]))

(defn app [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello, world!"})
   

(defn run []
  (jetty/run-jetty #'app {:port 8000 :join? false}))
            
