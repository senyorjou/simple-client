(ns simple-client.db
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :as log]            
            [clojure.java.jdbc :as jdbc]
            [postgre-types.json :refer [add-jsonb-type]]))

(add-jsonb-type
 json/write-str
 json/read-str)

(def db-spec
  {:dbtype "postgresql"
   :dbname "postgres"
   :user "postgres"
   :password "password"})

(defn insert-rows [table rows]
  (log/info "Writing" (count rows) "rows on table" (name table))
  (jdbc/insert-multi! db-spec table rows)
  nil)
