(ns simple-client.core
  (:require [digest :refer [sha-1]]
            ;;
            [simple-client.api :refer [get-users]]
            [simple-client.db :refer [upsert-rows]]))


(defn data-map [data-rows]
  (for [row data-rows]
    {:origin_id (get row "id")
     :data row
     :hash (sha-1 (str row))}))

(defn -main []
  (println "User management")
  (upsert-rows :slack_data (data-map (get-users))))
