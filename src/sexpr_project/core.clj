(ns sexpr-project.core
  (:gen-class)
  (:require [clojure.pprint :as pprint])
  (:require [sexpr-project.search :as search])
  (:require [sexpr-project.examples :as exml])
  (:require [clojure.edn :as edn])
  (:require [sexpr-project.modify :as modify])
  (:require [sexpr-project.validator :as validator])
  (:require [sexpr-project.html :as html]))


(defn open-edn-from-string [x]
  (edn/read-string (pr-str x)))

(def schema "{:orders [{:date nil :number nil :addresses [{:name nil :city nil :type nil :state nil :street nil :zip nil :country nil}] :items [{:ship_date nil :name nil :item nil :comment nil :quantity nil :price nil}]}]}")

(defn -main []
  ;(pprint/pprint (search/start-search "/orders/nothing/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders/addresses[%95819]/name/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders[0]/addresses[1]/name/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders/addresses/name[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders/*/name[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders/addresses[%\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders/*[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders/*[%\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/orders/*" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "~/name" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/start-search "/sssss/" (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/get-path 99503 (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/get-path (first (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/get-path (first (search/start-search "orders/addresses[%95819]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/get-path (first (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders)))
  ;(pprint/pprint (search/path-to-str (search/get-path (first (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders))))
  ;(pprint/pprint (search/path-to-str (search/get-path (first (search/start-search "orders/addresses[%95819]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders))))
  ;(pprint/pprint (search/path-to-str (search/get-path (first (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders))) (open-edn-from-string exml/ex_orders))))
  (pprint/pprint (search/path-to-str (search/get-path {:type "Shipping",
                                                       :name "Ellen Adams",
                                                       :street "123 Maple Street",
                                                       :city "Mill Valley",
                                                       :state "CA",
                                                       :zip 10999,
                                                       :country "USA"} (open-edn-from-string exml/ex_orders))))
  (pprint/pprint (search/path-to-str (search/get-path 99503 (open-edn-from-string exml/ex_orders))))
  (pprint/pprint (validator/validate-by-schema schema (modify/remove-field "/orders[0]/date/" (open-edn-from-string exml/ex_orders))))
  (pprint/pprint (validator/validate-part schema (search/get-path 99503 (open-edn-from-string exml/ex_orders))))
  (pprint/pprint (validator/validate-part schema '(:orders 0 :addresses 1 :name :nothing)))
  (pprint/pprint (search/start-search-with-validation "/orders/addresses[%95819]/name/" (open-edn-from-string exml/ex_orders) schema))
  (pprint/pprint (search/start-search-with-validation "/orders/addresses[%95819]/name/" (modify/add-field "/orders[0]/items[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh") schema))
  (pprint/pprint (search/start-search-with-validation "/orders[0]/items[0]/nothing/" (modify/add-field "/orders[0]/items[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh") schema))
  (pprint/pprint (modify/add-field-with-validation "/orders[0]/items[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh" schema))
  (html/example)
  )