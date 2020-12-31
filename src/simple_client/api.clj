(ns simple-client.api
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :as log]            
            [cemerick.url :refer (url)]
            [clj-http.client :as client]
            [dotenv :refer [env]])

  (:use [slingshot.slingshot :only [try+]]))


(def LIMIT 100)

(def root-url
  (->
   (url "https://slack.com/api")
   (assoc :query {:limit LIMIT})))

(defn make-url [path]
  (-> root-url
      (assoc :path (str (:path root-url) "/" path))))

(defn add-cursor [url cursor]
  (assoc-in url [:query :cursor] cursor))

(def users-url
  (make-url "users.list"))

(def acces-token
  (or (env "SLACK_TOKEN")
      (log/error "TOKEN not set")))

(def headers {:headers {:authorization (str "Bearer " acces-token)}})

(defn get-url [url]
  (log/info "Getting url:" url)
  (try+
   (json/read-str (:body (client/get url headers)))
   (catch [:status 404] {:keys [request-time body]}
     (log/warn "404" url request-time body))))

(defn get-response [url]
  (let [response (get-url url)
        ok (get response "ok")]
    (if ok
      {:response response
       :cursor (-> response (get "response_metadata") (get "next_cursor"))}
      (do
        (let [error (get response "error")]
          (log/warn "Error: " error)
          {:response error :cursor nil})))))
      
(defn get-data [url field]
  (loop [{response :response cursor :cursor} (get-response (str url))
         data []]
    (if (empty? cursor)
      (into data (get response field))
      (recur
       (get-response (str (add-cursor url cursor)))
       (into data (get response field))))))

(defn get-users []
  (get-data users-url "members"))
