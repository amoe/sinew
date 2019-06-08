(defproject sinew "0.1.0-SNAPSHOT"
  :description "Pluggable video cataloguer"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "9.4.1208.jre7"]
                 [ragtime "0.6.0"]
                 [clj-time "0.8.0"]
                 [clj-http "3.7.0"]
                 [com.taoensso/truss "1.5.0"]
                 [enlive "1.1.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [failjure "1.3.0"]
                 [com.gearswithingears/shrubbery "0.4.1"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [commons-io "2.4"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [compojure "1.6.1"]
                 [prone "0.8.0"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/tools.cli "0.3.1"]]

  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler sinew.server/lein-ring-handler}

  :aliases {"migrate"  ["run" "-m" "sinew.db-setup/migrate"]
            "rollback" ["run" "-m" "sinew.db-setup/rollback"]}

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
