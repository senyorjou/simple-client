(defproject simple-client "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [clj-http "3.11.0"]
                 [slingshot "0.12.2"]
                 [com.cemerick/url "0.1.1"]
                 [cheshire "5.10.0"]
                 [clj-jwt "0.1.1"]
                 [clj-time "0.15.2"]
                 [lynxeyes/dotenv "1.1.0"]
                 [digest "1.4.10"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.18"]
                 [postgre-types "0.0.4"]]
  
  :repl-options {:init-ns simple-client.core}
  :main simple-client.core)
