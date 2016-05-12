(defproject sinew "0.1.0-SNAPSHOT"
  :description "Cataloguer of videos"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 ; This postgres driver is not the current version but the
                 ; version recommended by java.jdbc upstream.
                 [postgresql/postgresql "8.4-702.jdbc4"]
                 [ragtime "0.3.8"]
                 [clj-time "0.8.0"]
                 [enlive "1.1.5"]
                 [commons-io "2.4"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [compojure "1.5.0"]
                 [prone "0.8.0"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/tools.cli "0.3.1"]]

  :plugins [[ragtime/ragtime.lein "0.3.8"]
            [lein-ring "0.9.1"]]

  :ragtime {:migrations ragtime.sql.files/migrations
            :database "jdbc:postgresql://kupukupu/amoe?user=amoe"}

  :ring {:handler sinew.server/app}
  :main sinew.add-to-db
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
