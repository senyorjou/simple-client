(ns simple-client.db
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :refer [join]]
            [clojure.tools.logging :as log]
            [postgre-types.json :refer [add-jsonb-type]]))

(add-jsonb-type
 json/write-str
 json/read-str)

(def db-spec
  {:dbtype "postgresql"
   :dbname "postgres"
   :user "postgres"
   :password "password"})

(defn- -id-hash-map [id-hashes]
  (into {} (for [row id-hashes] [(:origin_id row) (:hash row)])))

(defn get-id-hash-map [endpoint ids]
  (let [ids-str (map #(str "'" % "'") ids)       ;; builds a list of 'id1', 'id2'...
        ins (str "(" (join "," ids-str) ")")     ;; builds the IN clause "('id1', 'id2'...)"
        query (str "SELECT origin_id, hash FROM slack_data WHERE endpoint = '" endpoint "' AND origin_id in " ins)]
    (-id-hash-map (jdbc/query db-spec query))))

(defn- should-be-updated? [rows-cache row]
  (let [old-hash (get rows-cache (:origin_id row))
        new-hash (:hash row)]
    (and (not (nil? old-hash))
         (not (= old-hash new-hash)))))

(defn- should-be-inserted? [rows-cache row]
  (not (contains? rows-cache (:origin_id row))))


(defn upsert-rows [endpoint rows]
  (let [ids (map :origin_id rows)
        old-rows-cache (get-id-hash-map endpoint ids)
        to-insert (map #(assoc % :endpoint endpoint ) (filter (partial should-be-inserted? old-rows-cache) rows))
        to-update (map #(assoc % :endpoint endpoint ) (filter (partial should-be-updated? old-rows-cache) rows))]

    (log/info "Processing" (count rows) "rows for endpoint" endpoint)
    (log/info "Rows to insert" (count to-insert) "Rows to update" (count to-update))
    ;; insert
    (jdbc/insert-multi! db-spec :slack_data to-insert)
    ;; update... sigh
    (doseq [row to-update]
      (jdbc/update! db-spec :slack_data row ["endpoint=? AND origin_id=?" endpoint (:origin_id row)]))))
