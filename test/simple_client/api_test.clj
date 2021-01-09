(ns simple-client.api-test
  (:require [clojure.test :refer :all]
            [simple-client.api :refer :all]))

(deftest test-root-url
  (testing "Test should resolve root correctly"
    (is (= "https://slack.com/api?limit=500" (str root-url))
        "Should resolve to API root w/default limit")))

(deftest test-make-url
  (testing "Test should return a url with path"
    (let [url (make-url "foo")]
      (is (= "/api/foo" (:path url))
          "Should resolve to root/foo"))))

(deftest test-add-cursor
  (testing "Test should add a cursor query param"
    (let [url (add-cursor (make-url "foo") "123")]
        (is (= "123" (get-in url [:query :cursor]))
            "Should resolve to root w/ a cursor"))))
