(ns simple-client.core
  (:require [digest :refer [sha-1]]
            ;;
            [simple-client.api :refer [get-users]]
            [simple-client.db :refer [insert-rows]]))

(defn users-map
  "returns a map  {user_id {:origin_id user_id :data payload :hash payload_hash}}"
  []
  (into {} (map #(hash-map (get % "id")
                           {:origin_id (get % "id")
                            :data %
                            :hash (sha-1 (str %))})
                (get-users))))

(defn -main []
  (println "User management")
  (let [users (memoize users-map)]
    (insert-rows :slack_data (vals (users)))))
