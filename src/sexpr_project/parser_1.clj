(ns sexpr-project.parser_1
  (:require [clojure.string :as str]))

(defn parse-query 
  "Parse [query] into list. \"/\" symbol used of separation point"
  [query]
  ;; Разбиваем запрос на подстроки по символу "/"
  (remove str/blank? (str/split query #"/")))

(defn extract-conditions 
  "Separate condition in square brackets from [segment] into another string while keeping the brackets"
  [segment]
  ;; Извлекаем условия из сегмента, если они есть
  (let [condition-start (str/index-of segment "[")]
    (if condition-start
      [(subs segment 0 condition-start) (subs segment condition-start)]
      [segment])))

(defn extract-conditions-without-brackets 
  "Separate condition in square brackets from [segment] into another string while removing the brackets"
  [segment]
  ;; Извлекаем условия из сегмента, если они есть, убирая скобки
  (let [condition-start (str/index-of segment "[")
        condition-end (str/index-of segment "]")]
    (if condition-start
      [(keyword (subs segment 0 condition-start)) (Integer/parseInt (subs segment (inc condition-start) condition-end))]
      [(keyword segment)])))

(defn prepare-for-search 
  "Parse [query] string into list used for search"
  [query]
  ;; Преобразуем запрос в список сегментов и условий
  (let [segments (parse-query query)]
    ;(println "Parsed Query:" segments)
    segments))

(defn prepare-for-assoc 
  "Parse [query] string into list used by modifying functions"
  [query]
  ;; Преобразуем запрос в список сегментов и условий
  (let [segments (parse-query query)
        processed-segments (mapcat extract-conditions-without-brackets segments)]
    ;(println "Parsed Query:" processed-segments)
    processed-segments))

;; Пример
;(let [query "orders/*/name[%\"Ellen Adams\"]"]
;  (prepare-for-search query))
