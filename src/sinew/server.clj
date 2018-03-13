(ns sinew.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as wp]
            [clojure.pprint :as pprint]
            [failjure.core :as f]
            [sinew.filesystem :as filesystem]
            [sinew.system :as system]
            [clojure.tools.logging :refer [debugf warnf]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [sinew.select-by-tag :as select-by-tag]
            [sinew.data-service :as data]
            [prone.middleware :as prone]
            [net.cgrand.enlive-html :as html]
            [sinew.configuration :as configuration]
            [me.raynes.fs :as fs]))

(defn get-final-path [file-root scene]
  (str file-root "/" (:scene_type scene) "/" (:filename scene)))


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
  [:head :title] (html/content "Sinew | usage page"))

(defn time-or-bogus-value [filesystem path]
  (f/attempt-all [mtime (filesystem/get-mtime filesystem path)]
    mtime
    (f/when-failed [e]
      (warnf e)
      0)))


;; Not really sure what's going to happen when the file doesn't exist, but here
;; goes nothing...
(defn make-mtime-comparator [file-root filesystem]
  (fn [& scenes]
    (->> scenes
         (map #(get-final-path file-root %))
         (map #(time-or-bogus-value filesystem %))
         (apply compare))))

(defn scenes-sorted-by-mtime [file-root filesystem scene-data]
  (sort (make-mtime-comparator file-root filesystem)
        scene-data))

(defn render-index [repository tag-name]
  {:headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (search-result-template (data/query-by-tag repository tag-name))})

(defn render-list-all [{configuration :configuration
                        repository :repository
                        filesystem :filesystem}]
  (let [all-scenes (data/list-all-scenes repository)
        file-root (configuration/get-file-root configuration)]
    {:headers {"Content-Type" "text/html; charset=UTF-8"}
     :body (search-result-template (scenes-sorted-by-mtime file-root
                                                           filesystem
                                                           all-scenes))}))

(defn render-view-tags [repository]
  {:headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (view-tags-template (data/get-scenes-with-tags repository))})
  
(defn toggle-watched [repository plaintext-name]
  (data/toggle-watched repository plaintext-name)
  (str "Toggled watched status for " plaintext-name))


;; XXX: Possible abstraction of this let block, it's shared with render-list-all
(defn pick-next-scene [{configuration :configuration
                        repository :repository
                        filesystem :filesystem} watched?]
  (let [all-scenes (data/list-all-scenes repository)
        file-root (configuration/get-file-root configuration)]
    (->> (scenes-sorted-by-mtime file-root filesystem all-scenes)
         (filter #(= (:watched %) watched?))
         first
         :plaintext_name)))

;; returns a function which is the handler
(defn make-app [{repository :repository
                 configuration :configuration
                 filesystem :filesystem
                 :as system}]
  (-> (routes
       (GET "/" [] (main-template))
       (GET "/list" [] (render-list-all system))
       (GET "/view-tags" [] (render-view-tags repository))
       (GET "/tag/:tag-name" [tag-name] (render-index repository tag-name))
       (GET "/next-scene" {params :params}
            (pick-next-scene
             system
             (Boolean/valueOf (get params "watched"))))
       (GET "/toggle-watched/:name" [name]
            (toggle-watched repository name))
       (route/resources "/")
       (route/not-found "<h1>Page not found</h1>"))
      prone/wrap-exceptions
      wp/wrap-params))




(defn run []
  (let [system (system/build-system)]
    (jetty/run-jetty (make-app system)
                     {:port 8000 :join? false})))
            
