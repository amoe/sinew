(ns sinew.server
  (:require [ring.adapter.jetty :as jetty]
            [clojure.pprint :as pprint]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [sinew.select-by-tag :as select-by-tag]
            [sinew.data-service :as data]
            [prone.middleware :as prone]
            [net.cgrand.enlive-html :as html]
            [me.raynes.fs :as fs]))

(html/deftemplate search-result-template "templates/search-result.html"
  [file-list]
  [:ol :li.file] (html/clone-for [file file-list]
                   [:span.name] (html/content (:filename file))
                   [:span.watched]
                   (html/html-content
                    (if (:watched file) "&#x2713;" "&#x2717;"))
                   [:a.toggle-link] (html/set-attr
                                     :href (str "/toggle-watched/"
                                                (:plaintext_name file)))))

(html/deftemplate main-template "templates/index.html" []
  [:head :title] (html/content "bar"))

(defn scenes-sorted-by-mtime []
  (sort
   (fn [x y] (compare (get-mtime x) (get-mtime y)))
   (data/list-all-scenes)))



(defn render-index [tag-name]
  {:headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (search-result-template (data/query-by-tag tag-name))})

(defn render-list-all []
  {:headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (search-result-template (scenes-sorted-by-mtime))})


(defn toggle-watched [filename]
  (data/toggle-watched filename)
  (str "Toggled watched status for " filename))

(defn get-mtime [scene]
  (let [x (fs/mod-time (str
                "/mnt/nfs/kirk/genre/nasty/adultdoorway/" (:scene_type scene) "/"
                (:filename scene)))]
    (if (zero? x)
      (throw (Exception. (str "unable to get mtime for file: " (:filename scene)))))
    x))



(defn pick-next-scene []
   (:plaintext_name
    (first
     (filter (fn [x] (not (:watched x))) (scenes-sorted-by-mtime)))))


(def app
  (-> (routes
       (GET "/" [] "Hello, world!")
       (GET "/list" [] (render-list-all))
       (GET "/enlive-demo" [] (main-template))
       (GET "/tag/:tag-name" [tag-name] (render-index tag-name))
       (GET "/next-scene" []
            (pick-next-scene))
       (GET "/toggle-watched/:filename" [filename]
            (toggle-watched filename))
       (route/not-found "<h1>Page not found</h1>"))
      prone/wrap-exceptions))


(defn run []
  (jetty/run-jetty #'app {:port 8000 :join? false}))
            
