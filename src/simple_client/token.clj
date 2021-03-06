(ns simple-client.token
  (:require [cemerick.url :as url]
            [cheshire.core :as json]
            [clj-jwt.core :as jwt]
            [clj-jwt.key :as key]
            [clj-time.core :as time]
            [clj-http.client :as http]
            [clojure.string :as str]
            [dotenv :refer [env]])

  (:import java.io.StringReader))


;;; Example code for calling Google apis using a service account.
(defn load-creds
  "Takes a path to a service account .json credentials file"
  [secrets-json-path]
  (-> secrets-json-path slurp (json/parse-string keyword)))

(defn load-creds-from-env
  (-> (env "SERVICE_ACCOUNT")
      (json/parse-string keyword)))
  
;; list of API scopes requested, e.g. https://developers.google.com/admin-sdk/directory/v1/guides/authorizing
(def scopes ["https://www.googleapis.com/auth/admin.directory.user"
             "https://www.googleapis.com/auth/admin.directory.group"])


(defn create-claim [creds & [{:keys [sub] :as opts}]]
  (let [claim (merge {:iss (:client_email creds)
                      :scope (str/join " " scopes)
                      :aud "https://www.googleapis.com/oauth2/v4/token"
                      :exp (-> 1 time/hours time/from-now)
                      :iat (time/now)}
                     (when sub
                       ;; when using the Admin API, delegating access, :sub may be needed
                       {:sub sub}))]
    (-> claim jwt/jwt (jwt/sign :RS256 (-> creds :private_key (#(StringReader. %)) (#(key/pem->private-key % nil)))) (jwt/to-str))))

(defn request-token [creds & [{:keys [sub] :as opts}]]
  (let [claim (create-claim creds opts)
        resp (http/post "https://www.googleapis.com/oauth2/v4/token" {:form-params {:grant_type "urn:ietf:params:oauth:grant-type:jwt-bearer"
                                                                                    :assertion claim}
                                                                      :as :json})]
    (when (= 200 (-> resp :status))
      (-> resp :body :access_token))))

;; Call request-token to make an API call to google to create a new access token, using creds. Access-tokens are valid for 1 hour after creating. Pass the received token to API calls.

(defn api-req [{:keys [] :as request} token]
  (http/request (-> request
                    (assoc-in [:headers "Authorization"] (str "Bearer " token))
                    (assoc :as :json))))
(defn get-json [{:keys [] :as request}]
  (println request))
