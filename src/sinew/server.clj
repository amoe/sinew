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

(defn get-file-root []
  (-> "/usr/local/etc/sinew.edn" slurp read-string :file-root))

(defn get-final-path [scene]
  (str (get-file-root) "/" (:scene_type scene) "/" (:filename scene)))


(defn ^:private get-mtime-wrapper [scene]
  (let [final-path (get-final-path scene)]
    (let [result (fs/mod-time final-path)]
      {:final-path final-path
       :mtime result})))

(defn get-mtime-loose [scene]
  (-> scene get-mtime-wrapper :mtime))

(defn get-mtime-strict [scene]
  (let [result (get-mtime-wrapper scene)]
    (when (zero? (:mtime result))
      (throw 
       (Exception. 
        (str "file has 1970 mtime, or could not be read: " (:final-path result)))))))

(html/deftemplate search-result-template "templates/search-result.html"
  [file-list]
  [:table :tbody :tr.file_] (html/clone-for [file file-list]
                   [:td.name] (html/content (:filename file))
                   [:td.watched]
                   (html/html-content
                    (if (:watched file) "&#x2713;" "&#x2717;"))
                   [:td :a.toggle-link] (html/set-attr
                                         :href (str "/toggle-watched/"
                                                    (:plaintext_name file)))
                   [:td.description]
                   (html/html-content (:description file))
                   [:td.rating]
                   (html/html-content (:rating file))
                   [:td.scene_type]
                   (html/html-content (:scene_type file))))

(html/deftemplate view-tags-template "templates/view-tags.html"
  [file-list]
  [:table :tbody :tr.file_] (html/clone-for [file file-list]
                   [:td.name] (html/content (:name file))
                   [:td.tags :ul :li.tag]
                   (html/clone-for [tag (:tags file)]
                     [:a.search-tag-link]
                     (html/content tag)
                     [:a.search-tag-link]
                     (html/set-attr :href (str "/tag/" tag)))))



(html/deftemplate main-template "templates/index.html" []
  [:head :title] (html/content "bar"))

;; Not really sure what's going to happen when the file doesn't exist, but here
;; goes nothing...
(defn compare-scenes [x y]
  (compare (get-mtime-loose x) (get-mtime-loose y)))

(defn scenes-sorted-by-mtime []
  (sort compare-scenes
        (data/list-all-scenes)))

(defn render-index [tag-name]
  {:headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (search-result-template (data/query-by-tag tag-name))})

(defn render-list-all []
  {:headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (search-result-template (scenes-sorted-by-mtime))})

(defn render-view-tags []
  {:headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (view-tags-template (data/get-scenes-with-tags))})
  
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
       (GET "/view-tags" [] (render-view-tags))
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
            
