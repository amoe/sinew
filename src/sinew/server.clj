(ns sinew.server
  (:require [ring.adapter.jetty :as jetty]
            [clojure.pprint :as pprint]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [sinew.select-by-tag :as select-by-tag]
            [sinew.data-service :as data]
            [prone.middleware :as prone]
            [net.cgrand.enlive-html :as html]))


(html/deftemplate main-template "templates/index.html" []
  [:head :title] (html/content "bar"))

(defn render-index [tag-name]
  {:headers {"Content-Type" "text/plain"}
   :body (with-out-str
           (pprint/pprint (select-by-tag/query-by-tag tag-name)))})

(defn toggle-watched [filename]
  (data/toggle-watched filename)
  (str "Toggled watched status for " filename))

(def app
  (-> (routes
       (GET "/" [] "Hello, world!")
       (GET "/enlive-demo" [] (main-template))
       (GET "/tag/:tag-name" [tag-name] (render-index tag-name))
       (GET "/toggle-watched/:filename" [filename]
            (toggle-watched filename))
       (route/not-found "<h1>Page not found</h1>"))
      prone/wrap-exceptions))


(defn run []
  (jetty/run-jetty #'app {:port 8000 :join? false}))
            
