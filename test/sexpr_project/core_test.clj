(ns sexpr-project.core-test
  (:require [clojure.test :refer :all])
  (:require [sexpr-project.examples :as exml])
  (:require [clojure.edn :as edn])
  (:require [sexpr-project.search :as search])
  (:require [sexpr-project.modify :as modify])
  (:require [sexpr-project.validator :as validator])
  (:require [sexpr-project.html :as html]))

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
    (is (= (search/get-path 99503 (open-edn-from-string exml/ex_orders)) [:orders 0 :number]))
    (is (= (search/get-path (first (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders)) nil))
    (is (= (search/get-path (first (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders)) [:orders 0 :addresses 1 :name]))
    (is (= (search/get-path (first (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders)) [:orders 0 :addresses 1 :name]))
    (is (= (search/path-to-str (search/get-path (first (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders))) ""))
    (is (= (search/path-to-str (search/get-path (first (search/start-search "orders/addresses[%95819]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders))) "/orders[0]/addresses[1]/name"))
    (is (= (search/path-to-str (search/get-path (first (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders))) "/orders[0]/addresses[1]/name")))

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
    (is (not (validator/validate-by-schema schema (open-edn-from-string exml/ex_catalog))))
    (is (validator/validate-part schema (search/get-path 99503 (open-edn-from-string exml/ex_orders))))
    (is (not (validator/validate-part schema '(:orders 0 :addresses 1 :name :nothing))))
    )
  
  (testing "HTML generation"
    (is (= (html/generate-html (open-edn-from-string exml/ex_orders)) "<div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>orders</th></tr></thead><tbody><tr><td><div><ul><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>number</th><th>date</th><th>addresses</th><th>items</th></tr></thead><tbody><tr><td><div><p>99503</p></div></td><td><div><p>1999-10-20</p></div></td><td><div><ul><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>type</th><th>name</th><th>street</th><th>city</th><th>state</th><th>zip</th><th>country</th></tr></thead><tbody><tr><td><div><p>Shipping</p></div></td><td><div><p>Ellen Adams</p></div></td><td><div><p>123 Maple Street</p></div></td><td><div><p>Mill Valley</p></div></td><td><div><p>CA</p></div></td><td><div><p>10999</p></div></td><td><div><p>USA</p></div></td></tr></tbody></table></div></li><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>type</th><th>name</th><th>street</th><th>city</th><th>state</th><th>zip</th><th>country</th></tr></thead><tbody><tr><td><div><p>Billing</p></div></td><td><div><p>Tai Yee</p></div></td><td><div><p>8 Oak Avenue</p></div></td><td><div><p>Old Town</p></div></td><td><div><p>PA</p></div></td><td><div><p>95819</p></div></td><td><div><p>USA</p></div></td></tr></tbody></table></div></li></ul></div></td><td><div><ul><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>item</th><th>name</th><th>quantity</th><th>price</th><th>comment</th></tr></thead><tbody><tr><td><div><p>872-AA</p></div></td><td><div><p>Lawnmower</p></div></td><td><div><p>1</p></div></td><td><div><p>148.95</p></div></td><td><div><p>comment</p></div></td></tr></tbody></table></div></li><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>item</th><th>name</th><th>quantity</th><th>price</th><th>ship_date</th></tr></thead><tbody><tr><td><div><p>926-AA</p></div></td><td><div><p>Baby Monitor</p></div></td><td><div><p>2</p></div></td><td><div><p>39.98</p></div></td><td><div><p>1999-05-21</p></div></td></tr></tbody></table></div></li></ul></div></td></tr></tbody></table></div></li></ul></div></td></tr></tbody></table></div>"))
    (is (= (html/generate-html (open-edn-from-string exml/ex_vehicles)) "<div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>vehicles</th></tr></thead><tbody><tr><td><div><ul><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>number</th><th>manufacturer</th><th>color</th><th>loans</th><th>tires</th><th>engine</th></tr></thead><tbody><tr><td><div><p>AE368UK</p></div></td><td><div><p>Toyota</p></div></td><td><div><p>Red</p></div></td><td><div><ul><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>reoccuring</th><th>owners</th></tr></thead><tbody><tr><td><div><p>Monthly</p></div></td><td><div><ul><li><div><p>Bob</p></div></li></ul></div></td></tr></tbody></table></div></li></ul></div></td><td><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>model</th><th>size</th></tr></thead><tbody><tr><td><div><p>123123</p></div></td><td><div><p>23</p></div></td></tr></tbody></table></div></td><td><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>model</th></tr></thead><tbody><tr><td><div><p>30065</p></div></td></tr></tbody></table></div></td></tr></tbody></table></div></li></ul></div></td></tr></tbody></table></div>"))
    (is (= (html/generate-html (open-edn-from-string exml/ex_catalog)) "<div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>catalog</th></tr></thead><tbody><tr><td><div><ul><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>book</th><th>author</th><th>title</th><th>genre</th><th>price</th><th>publish_date</th><th>description</th></tr></thead><tbody><tr><td><div><p>bk101</p></div></td><td><div><p>Gambardella, Matthew</p></div></td><td><div><p>XML Developer's Guide</p></div></td><td><div><p>Computer</p></div></td><td><div><p>44.95</p></div></td><td><div><p>2000-10-01</p></div></td><td><div><p>An in-depth look at creating applications with XML.</p></div></td></tr></tbody></table></div></li><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>book</th><th>author</th><th>title</th><th>genre</th><th>price</th><th>publish_date</th><th>description</th></tr></thead><tbody><tr><td><div><p>bk102</p></div></td><td><div><p>Ralls, Kim</p></div></td><td><div><p>Midnight Rain</p></div></td><td><div><p>Fantasy</p></div></td><td><div><p>5.95</p></div></td><td><div><p>2000-12-16</p></div></td><td><div><p>A former architect battles corporate zombies, an evil sorceress, and her own childhood to become queen of the world.</p></div></td></tr></tbody></table></div></li><li><div><table border=\"1\" style=\"border: solid 1px lightgrey;\"><thead><tr><th>book</th><th>author</th><th>title</th><th>genre</th><th>price</th><th>publish_date</th><th>description</th></tr></thead><tbody><tr><td><div><p>bk103</p></div></td><td><div><p>Corets, Eva</p></div></td><td><div><p>Maeve Ascendant</p></div></td><td><div><p>Fantasy</p></div></td><td><div><p>5.95</p></div></td><td><div><p>2000-11-17</p></div></td><td><div><p>After the collapse of a nanotechnology society in England, the young survivors lay the foundation for a new society.</p></div></td></tr></tbody></table></div></li></ul></div></td></tr></tbody></table></div>"))
  ))
