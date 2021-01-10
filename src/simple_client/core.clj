(ns simple-client.core
  (:require [digest :refer [sha-1]]
            ;;
            [simple-client.api :refer [get-users get-conversations]]
            [simple-client.db :as db]))

(defn data-map [data-rows]
  (for [row data-rows]
    {:origin_id (get row "id")
     :data row
     :hash (sha-1 (str row))}))

(defn -main []
  (println "User management")
  (db/upsert-rows "users.list" (data-map (get-users)))
  (db/upsert-rows "conversations.list" (data-map (get-conversations))))
