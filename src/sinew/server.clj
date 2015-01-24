(ns sinew.server
  (:require [ring.adapter.jetty :as jetty]
            [clojure.pprint :as pprint]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [sinew.select-by-tag :as select-by-tag]
            [sinew.data-service :as data]
            [prone.middleware :as prone]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate search-result-template "templates/search-result.html"
  [file-list]
  [:ul :li.file] (html/clone-for [file file-list]
                   [:span.name] (html/content (:filename file))
                   [:a.toggle-link] (html/set-attr
                                     :href (str "/toggle-watched/"
                                                (:plaintext_name file)))))

(html/deftemplate main-template "templates/index.html" []
  [:head :title] (html/content "bar"))

(defn render-index [tag-name]
  {:headers {"Content-Type" "text/html"}
   :body (search-result-template (data/query-by-tag tag-name))})

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
            
