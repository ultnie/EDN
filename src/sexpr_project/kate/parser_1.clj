(ns parser_1
  (:require [clojure.string :as str]))

(defn parse-query [query]
  ;; Разбиваем запрос на подстроки по символу "/"
  (str/split query #"/"))

(defn extract-conditions [segment]
  ;; Извлекаем условия из сегмента, если они есть
  (let [condition-start (str/index-of segment "[")]
    (if condition-start
      [(subs segment 0 condition-start) (subs segment condition-start)]
      [segment])))

(defn prepare-for-search [query]
  ;; Преобразуем запрос в список сегментов и условий
  (let [segments (parse-query query)
        processed-segments (mapcat extract-conditions segments)]
    (println "Parsed Query:" processed-segments)
    processed-segments))

;; Пример
(let [query "orders/*/name[%\"Ellen Adams\"]"]
  (prepare-for-search query))
