(ns sinew.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as wp]
            [clojure.pprint :as pprint]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [sinew.select-by-tag :as select-by-tag]
            [sinew.data-service :as data]
            [prone.middleware :as prone]
            [net.cgrand.enlive-html :as html]
            [me.raynes.fs :as fs]))

(defn get-mtime [scene]
  (let [x (fs/mod-time (str
                "/mnt/nfs/kirk/genre/nasty/adultdoorway/" (:scene_type scene) "/"
                (:filename scene)))]
    (if (zero? x)
      (throw (Exception. (str "unable to get mtime for file: " (:filename scene)))))
    x))




(html/deftemplate search-result-template "templates/search-result.html"
  [file-list]
  [:table :tr.file] (html/clone-for [file file-list]
                   [:td.name] (html/content (:filename file))
                   [:td.watched]
                   (html/html-content
                    (if (:watched file) "&#x2713;" "&#x2717;"))
                   [:td :a.toggle-link] (html/set-attr
                                         :href (str "/toggle-watched/"
                                                    (:plaintext_name file)))
                   [:td.description]
                   (html/html-content (:description file))
                   [:td.scene_type]
                   (html/html-content (:scene_type file))))

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


(defn toggle-watched [plaintext-name]
  (data/toggle-watched plaintext-name)
  (str "Toggled watched status for " plaintext-name))



(defn pick-next-scene [watched?]
   (:plaintext_name
    (first
     (filter (fn [x] (= (:watched x) watched?)) (scenes-sorted-by-mtime)))))

(def app
  (-> (routes
       (GET "/" [] (main-template))
       (GET "/list" [] (render-list-all))
       (GET "/tag/:tag-name" [tag-name] (render-index tag-name))
       (GET "/next-scene" {params :params}
            (pick-next-scene
             (Boolean/valueOf (get params "watched"))))
       (GET "/toggle-watched/:name" [name]
            (toggle-watched name))
       (route/not-found "<h1>Page not found</h1>"))
      prone/wrap-exceptions
      wp/wrap-params))


(defn run []
  (jetty/run-jetty #'app {:port 8000 :join? false}))
            
