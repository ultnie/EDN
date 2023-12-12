(ns sexpr-project.core
  (:gen-class)
  (:require clojure.pprint)
  (:require [sexpr-project.examples :as exml])
  (:require clojure.edn))

(defn verify-edn [x]
  (clojure.edn/read-string (pr-str x)))

; * - match all
; [] - optional selector 
;   [0] - select by index, [="text"] - specific value equals "text", [%"text"] - one of the values equals "text"
; catalog/* - return all books
; catalog/genre[="Fantasy"] - return books with genre Fantasy
; catalog[%"Fantasy"] - same, but without telling that it is genre, or if something else also equals "Fantasy" it should be returned
; catalog[2] - return the third object on catalog
; orders/*/name[%"Ellen Adams"] - return everything in "orders" that has a field name
; как сделать относительный путь, схему? Модификацию, по идее, можно через мапу.

(defn -main []
  (clojure.pprint/pprint (verify-edn exml/ex_orders))
  (clojure.pprint/pprint (verify-edn exml/ex_vehicles))
  (clojure.pprint/pprint (verify-edn exml/ex_catalog)))