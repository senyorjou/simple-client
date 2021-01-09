(ns simple-client.core-test
  (:require [clojure.test :refer :all]
            [simple-client.core :refer :all]))

(deftest test-data-map
  (testing "Test data-map transforms correctly"
    (let [input [{"id" "id" "data" "data"}]
          got (data-map input)
          want '({:origin_id "id"
                 :data {"id" "id" "data" "data"}
                 :hash "3e402367bbce531e8a7186ec659611854a171b1c"})]
      (is (= (count got) 1) "There should be only one element")
      (is (= got want) "Element has 3 keys, being hash a sha-1"))))
