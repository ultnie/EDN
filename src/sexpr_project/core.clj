(ns sexpr-project.core
  (:gen-class)
  (:require [clojure.pprint :as pprint])
  (:require [sexpr-project.examples :as exml])
  (:require [clojure.edn :as edn])
  (:require [sexpr-project.search :as search])
  (:require [sexpr_project.modify :as modify])
  (:require [sexpr-project.validator :as validator])
  )

(defn open-edn-from-string [x]
  (edn/read-string (pr-str x)))

(def schema "{:orders [{:date nil :number nil :addresses [{:name nil :city nil :type nil :state nil :street nil :zip nil :country nil}] :items [{:ship_date nil :name nil :item nil :comment nil :quantity nil :price nil}]}]}")

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
  )