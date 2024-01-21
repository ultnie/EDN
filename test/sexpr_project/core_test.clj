(ns sexpr-project.core-test
  (:require [clojure.test :refer :all])
  (:require [clojure.pprint :as pprint])
  (:require [sexpr-project.examples :as exml])
  (:require [clojure.edn :as edn])
  (:require [sexpr-project.search :as search])
  (:require [sexpr_project.modify :as modify])
  (:require [sexpr-project.validator :as validator]))

(defn open-edn-from-string [x]
  (edn/read-string (pr-str x)))

(def schema "{:orders [{:date nil :number nil :addresses [{:name nil :city nil :type nil :state nil :street nil :zip nil :country nil}] :items [{:ship_date nil :name nil :item nil :comment nil :quantity nil :price nil}]}]}")

(deftest a-test
  (testing "Search"
    (is (= (search/start-search "/orders/nothing/" (open-edn-from-string exml/ex_orders)) (list)))
    (is (= (search/start-search "/orders/addresses[%95819]/name/" (open-edn-from-string exml/ex_orders)) (list "Tai Yee")))
    (is (= (search/start-search "/orders[0]/addresses[1]/name/" (open-edn-from-string exml/ex_orders)) (search/start-search "/orders/addresses[%95819]/name/" (open-edn-from-string exml/ex_orders))))
    (is (= (search/start-search "/orders/addresses/name[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)) (list {:type "Shipping",
                                                                                                                          :name "Ellen Adams",
                                                                                                                          :street "123 Maple Street",
                                                                                                                          :city "Mill Valley",
                                                                                                                          :state "CA",
                                                                                                                          :zip 10999,
                                                                                                                          :country "USA"})))
    (is (= (search/start-search "/orders/*/name[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)) (list {:type "Shipping",
                                                                                                                  :name "Ellen Adams",
                                                                                                                  :street "123 Maple Street",
                                                                                                                  :city "Mill Valley",
                                                                                                                  :state "CA",
                                                                                                                  :zip 10999,
                                                                                                                  :country "USA"})))
    (is (= (search/start-search "/orders/addresses[%\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)) (list {:type "Shipping",
                                                                                                                     :name "Ellen Adams",
                                                                                                                     :street "123 Maple Street",
                                                                                                                     :city "Mill Valley",
                                                                                                                     :state "CA",
                                                                                                                     :zip 10999,
                                                                                                                     :country "USA"})))
    (is (= (search/start-search "/orders/*[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)) (list {:type "Shipping",
                                                                                                             :name "Ellen Adams",
                                                                                                             :street "123 Maple Street",
                                                                                                             :city "Mill Valley",
                                                                                                             :state "CA",
                                                                                                             :zip 10999,
                                                                                                             :country "USA"})))
    (is (= (search/start-search "/orders/*[%\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)) (list {:type "Shipping",
                                                                                                             :name "Ellen Adams",
                                                                                                             :street "123 Maple Street",
                                                                                                             :city "Mill Valley",
                                                                                                             :state "CA",
                                                                                                             :zip 10999,
                                                                                                             :country "USA"})))
    (search/start-search "/orders/*" (open-edn-from-string exml/ex_orders))
    (is (= (search/start-search "~/name" (open-edn-from-string exml/ex_orders)) (list "Ellen Adams" "Tai Yee" "Lawnmower" "Baby Monitor")))
    (is (= (search/start-search "/sssss/" (open-edn-from-string exml/ex_orders)) (list))))

  (testing "Path"
    (is (= (search/get-path 99503 (open-edn-from-string exml/ex_orders)) (list :orders 0 :number)))
    (is (= (search/get-path (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders)) (list)))
    (is (= (search/get-path (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders)) (list :orders 0 :addresses 1 :name)))
    (is (= (search/get-path (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders)) (list :orders 0 :addresses 1 :name)))
    (is (= (search/path-to-str (search/get-path (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders))) ""))
    (is (= (search/path-to-str (search/get-path (search/start-search "orders/addresses[%95819]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders))) "/orders[0]/addresses[1]/name"))
    (is (= (search/path-to-str (search/get-path (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders))) "/orders[0]/addresses[1]/name")))

  (testing "Modification"
    (is (= (list (modify/add-field "/orders[1]/nothing/" (open-edn-from-string exml/ex_orders) "heh")) (list {:orders [{:number 99503,
                                                                                                                        :date "1999-10-20",
                                                                                                                        :addresses
                                                                                                                        [{:type "Shipping",
                                                                                                                          :name "Ellen Adams",
                                                                                                                          :street "123 Maple Street",
                                                                                                                          :city "Mill Valley",
                                                                                                                          :state "CA",
                                                                                                                          :zip 10999,
                                                                                                                          :country "USA"}
                                                                                                                         {:type "Billing",
                                                                                                                          :name "Tai Yee",
                                                                                                                          :street "8 Oak Avenue",
                                                                                                                          :city "Old Town",
                                                                                                                          :state "PA",
                                                                                                                          :zip 95819,
                                                                                                                          :country "USA"}],
                                                                                                                        :items
                                                                                                                        [{:item "872-AA",
                                                                                                                          :name "Lawnmower",
                                                                                                                          :quantity 1,
                                                                                                                          :price 148.95,
                                                                                                                          :comment "comment"}
                                                                                                                         {:item "926-AA",
                                                                                                                          :name "Baby Monitor",
                                                                                                                          :quantity 2,
                                                                                                                          :price 39.98,
                                                                                                                          :ship_date "1999-05-21"}]}
                                                                                                                       {:nothing "heh"}]})))
    (is (= (list (modify/add-field "/orders[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh")) (list {:orders
                                                                                                              [{:number 99503,
                                                                                                                :date "1999-10-20",
                                                                                                                :addresses
                                                                                                                [{:type "Shipping",
                                                                                                                  :name "Ellen Adams",
                                                                                                                  :street "123 Maple Street",
                                                                                                                  :city "Mill Valley",
                                                                                                                  :state "CA",
                                                                                                                  :zip 10999,
                                                                                                                  :country "USA"}
                                                                                                                 {:type "Billing",
                                                                                                                  :name "Tai Yee",
                                                                                                                  :street "8 Oak Avenue",
                                                                                                                  :city "Old Town",
                                                                                                                  :state "PA",
                                                                                                                  :zip 95819,
                                                                                                                  :country "USA"}],
                                                                                                                :items
                                                                                                                [{:item "872-AA",
                                                                                                                  :name "Lawnmower",
                                                                                                                  :quantity 1,
                                                                                                                  :price 148.95,
                                                                                                                  :comment "comment"}
                                                                                                                 {:item "926-AA",
                                                                                                                  :name "Baby Monitor",
                                                                                                                  :quantity 2,
                                                                                                                  :price 39.98,
                                                                                                                  :ship_date "1999-05-21"}],
                                                                                                                :nothing "heh"}]})))
    (is (= (list (modify/add-field "/orders[0]/items[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh")) (list {:orders
                                                                                                                       [{:number 99503,
                                                                                                                         :date "1999-10-20",
                                                                                                                         :addresses
                                                                                                                         [{:type "Shipping",
                                                                                                                           :name "Ellen Adams",
                                                                                                                           :street "123 Maple Street",
                                                                                                                           :city "Mill Valley",
                                                                                                                           :state "CA",
                                                                                                                           :zip 10999,
                                                                                                                           :country "USA"}
                                                                                                                          {:type "Billing",
                                                                                                                           :name "Tai Yee",
                                                                                                                           :street "8 Oak Avenue",
                                                                                                                           :city "Old Town",
                                                                                                                           :state "PA",
                                                                                                                           :zip 95819,
                                                                                                                           :country "USA"}],
                                                                                                                         :items
                                                                                                                         [{:item "872-AA",
                                                                                                                           :name "Lawnmower",
                                                                                                                           :quantity 1,
                                                                                                                           :price 148.95,
                                                                                                                           :comment "comment",
                                                                                                                           :nothing "heh"}
                                                                                                                          {:item "926-AA",
                                                                                                                           :name "Baby Monitor",
                                                                                                                           :quantity 2,
                                                                                                                           :price 39.98,
                                                                                                                           :ship_date "1999-05-21"}]}]})))
    (is (thrown-with-msg? Exception #"That field already exists" (modify/add-field "/orders[0]/items[0]/name/" (open-edn-from-string exml/ex_orders) "heh")))
    (is (= (list (modify/remove-field "/orders[0]/date/" (open-edn-from-string exml/ex_orders))) (list {:orders
                                                                                                        [{:number 99503,
                                                                                                          :addresses
                                                                                                          [{:type "Shipping",
                                                                                                            :name "Ellen Adams",
                                                                                                            :street "123 Maple Street",
                                                                                                            :city "Mill Valley",
                                                                                                            :state "CA", :zip 10999,
                                                                                                            :country "USA"}
                                                                                                           {:type "Billing",
                                                                                                            :name "Tai Yee",
                                                                                                            :street "8 Oak Avenue",
                                                                                                            :city "Old Town",
                                                                                                            :state "PA",
                                                                                                            :zip 95819,
                                                                                                            :country "USA"}],
                                                                                                          :items
                                                                                                          [{:item "872-AA",
                                                                                                            :name "Lawnmower",
                                                                                                            :quantity 1,
                                                                                                            :price 148.95,
                                                                                                            :comment "comment"}
                                                                                                           {:item "926-AA",
                                                                                                            :name "Baby Monitor",
                                                                                                            :quantity 2,
                                                                                                            :price 39.98,
                                                                                                            :ship_date "1999-05-21"}]}]})))
    (is (thrown-with-msg? Exception #"Can't remove, field doesn't exist" (modify/remove-field "/orders[0]/nothing/" (open-edn-from-string exml/ex_orders))))
    (is (= (list (modify/modify-field "/orders[0]/date/" (open-edn-from-string exml/ex_orders) "heh")) (list {:orders
                                                                                                              [{:number 99503,
                                                                                                                :date "heh",
                                                                                                                :addresses
                                                                                                                [{:type "Shipping",
                                                                                                                  :name "Ellen Adams",
                                                                                                                  :street "123 Maple Street",
                                                                                                                  :city "Mill Valley",
                                                                                                                  :state "CA",
                                                                                                                  :zip 10999,
                                                                                                                  :country "USA"}
                                                                                                                 {:type "Billing",
                                                                                                                  :name "Tai Yee",
                                                                                                                  :street "8 Oak Avenue",
                                                                                                                  :city "Old Town",
                                                                                                                  :state "PA",
                                                                                                                  :zip 95819,
                                                                                                                  :country "USA"}],
                                                                                                                :items
                                                                                                                [{:item "872-AA",
                                                                                                                  :name "Lawnmower",
                                                                                                                  :quantity 1,
                                                                                                                  :price 148.95,
                                                                                                                  :comment "comment"}
                                                                                                                 {:item "926-AA",
                                                                                                                  :name "Baby Monitor",
                                                                                                                  :quantity 2,
                                                                                                                  :price 39.98,
                                                                                                                  :ship_date "1999-05-21"}]}]})))
    (is (thrown-with-msg? Exception #"Can't modify, field doesn't exist" (modify/modify-field "/orders[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh"))))

  (testing "Validation"
    (is (validator/validate-by-schema schema (open-edn-from-string exml/ex_orders)))
    (is (not (validator/validate-by-schema schema (modify/add-field "/orders[0]/items[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh"))))
    (is (validator/validate-by-schema schema (modify/remove-field "/orders[0]/date/" (open-edn-from-string exml/ex_orders))))
    (is (validator/validate-by-schema schema (modify/modify-field "/orders[0]/date/" (open-edn-from-string exml/ex_orders) "heh")))
    (is (validator/validate-by-schema schema (edn/read-string "{:orders []}")))
    (is (not (validator/validate-by-schema schema (open-edn-from-string exml/ex_catalog))))))