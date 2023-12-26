(ns sexpr-project.core
  (:gen-class)
  (:require [clojure.pprint :as pprint])
  (:require [sexpr-project.examples :as exml])
  (:require [clojure.edn :as edn])
  (:require [sexpr-project.search :as search])
  (:require [sexpr_project.modify :as modify])
  )

(defn open-edn-from-string [x]
  (edn/read-string (pr-str x)))

; * - match all
; [] - optional selector 
;   [0] - select by index, [="text"] - specific value equals "text", [%"text"] - one of the values equals "text"
; catalog/* - return all books
; catalog/genre[="Fantasy"] - return books with genre Fantasy
; catalog[%"Fantasy"] - same, but without telling that it is genre, or if something else also equals "Fantasy" it should be returned
; catalog[2] - return the third object on catalog
; orders/*/name[%"Ellen Adams"] - return everything in "orders" that has a field name
; ~name или ~/name - относительный поиск
; как сделать относительный путь (сохранять предыдущий запрос и склеивать с относительным), схему (обход)? Модификацию, по идее, можно через мапу.

(defn -main []
  ;(pprint/pprint (verify-edn exml/ex_orders))
  ;(pprint/pprint (verify-edn exml/ex_vehicles))
  ;(pprint/pprint (verify-edn exml/ex_catalog))
  ;(println (parser/prepare-for-search "orders"))
  (pprint/pprint (search/start-search "/orders/nothing/" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "/orders/addresses[%95819]/name/" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "/orders[0]/addresses[1]/name/" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "/orders/addresses/name[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "/orders/*/name[=\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "/orders/addresses[%\"Ellen Adams\"]/" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "/orders/*" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "~/name" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (search/start-search "/sssss/" (open-edn-from-string exml/ex_orders)))
  (println (search/get-path (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders)))
  (println (search/get-path (search/start-search "orders/addresses[%95819]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders)))
  (println (search/get-path (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders)))
  (println (search/path-to-str (search/get-path (search/start-search "orders/nothing" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders))))
  (println (search/path-to-str (search/get-path (search/start-search "orders/addresses[%95819]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders))))
  (println (search/path-to-str (search/get-path (search/start-search "orders[0]/addresses[1]/name" (open-edn-from-string exml/ex_orders)) (open-edn-from-string exml/ex_orders))))
  (pprint/pprint (modify/add-field "/orders[1]/nothing/" (open-edn-from-string exml/ex_orders) "heh"))
  (pprint/pprint (modify/add-field "/orders[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh"))
  (pprint/pprint (modify/add-field "/orders[0]/items[0]/nothing/" (open-edn-from-string exml/ex_orders) "heh"))
  (pprint/pprint (modify/remove-field "/orders[0]/date/" (open-edn-from-string exml/ex_orders)))
  (pprint/pprint (modify/modify-field "/orders[0]/date/" (open-edn-from-string exml/ex_orders) "heh")))