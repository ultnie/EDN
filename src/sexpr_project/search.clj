(ns sexpr-project.search
  (:require [clojure.string :as str]
            [sexpr-project.validator :as validator])
  (:require [sexpr-project.parser_1 :as parser])
  )

(def ^:dynamic *prev_query* "") ; переменная, где хранится предыдущий запрос

; в конце каждой ветви вызывается (search ((rest query) new_data)), где new_data - суженная область поиска данных в зависимости от прошлого результата

(defn path-to-str
  "Transforms list [path] to string according to the query language. Resulting string can be used by functions in sexpr_project.modify file"
  [path]
  (let [res ""]
    (apply str (doall (for [i path] (if (number? i)
                                      (str res "[" i "]")
                                      (str res "/" (name i))))))))

(defn get-paths
  "Finds paths to [endpoint] values in nested [data] map. Returns list of vectors with all the keys and indexes on those paths. Returns empty list if there's no [endpoint] in [data]"
  [endpoint data]
  (let [paths (validator/kvpaths-all data)]
    (remove nil? (doall (for [i paths] (if (= endpoint (get-in data i)) i))))))

(defn get-path
  "Finds first path to [endpoint] value in nested [data] map"
  [endpoint data]
    (let [paths (validator/kvpaths-all data)]
      (first (get-paths endpoint data))
  ))

(defn- search
  "Searches data specified by [query] in [data]. Returns a list of all data that corresponds to the [query]"
  [query data]

  (let [query_first (first query)
        cond-search (fn [query data]
                      (cond
                        (str/includes? query_first "[") (let [q (parser/extract-conditions query_first) ; с условием
                                                              tag (first q)
                                                              condition (subs (second q) (inc (str/index-of (second q) "[")) (str/index-of (second q) "]"))]
                                                                          ;(println (str/replace (subs condition 1) #"\"" ""))
                                                                          ;(println (get-in i [(keyword tag)]))
                                                          (cond
                                                            (= (str tag) "*") (get-in data (butlast (get-path (str/replace (apply str (rest condition)) #"\"" "") data)))
                                                            (= (str (get condition 0)) "=") (if (= (str (get-in data [(keyword tag)])) (str/replace (subs condition 1) #"\"" "")) ; если требуется равентсво данных в определённом поле
                                                                                              (search (rest query) data)
                                                                                              nil)
                                                            (= (str (get condition 0)) "%") (search (rest query) (remove nil? (doall (for [i (get-in data [(keyword tag)])] (remove empty? (doall (for [j (keys i)] (search [(str (subs (str j) 1) "[=" (subs condition 1) "]")] i)))))))) ;если требуется чтобы какое-то поле ниже было равно данным
                                                            (number? (Integer/parseInt condition)) (search (rest query) (get-in data [(keyword tag) (Integer/parseInt condition)])))); если индекс
                        (= (str (get query_first 0)) "*") (search (rest query) (vals data)) ; всё на подуровне
                        :else (search (rest query) (get-in data [(keyword query_first)]))))]
    ;(println "Query: " query)
    ;(println (type query))
    ;(pprint/pprint data)
    (if (empty? query)
      (list data) ; если запрос закончился, то возвращаем найденные данные
      (cond
        (map? data) (cond-search query data)
        (vector? data) (doall (for [i data] (cond-search query i)))
        (seq? data) (doall (for [i data] (search query i)))
        :else nil))))

(defn start-search 
  "Remembers the [query] in case next time search will be relative and starts a search function to find data specified by [query] in [data]. Returns a list of all data that corresponds to the [query]"
  [query data]
  (if (not (= (str (get query 0)) "~")) ;если первый символ в строке ~, то относительный поиск
    (alter-var-root #'*prev_query* (constantly query))
    (alter-var-root #'*prev_query* (constantly (str *prev_query* (subs query 1))))) ;прикрепляем к прошлому поиску новую часть запроса
  ;(println *prev_query*)
  (remove nil? (flatten (search (parser/prepare-for-search *prev_query*) data))))

(defn start-search-with-validation
  "start-search that validates found result to check if it should be in the document according to the schema"
  [query data schema]
  (let [result (start-search query data)
        valid-res (doall (for [i result] (validator/validate-part schema (get-path i data))))]
    (doall (for [i (range (count valid-res))] (if (false? (nth valid-res i)) (println (str "WARNING! Path " (get-path (nth result i) data) " is not in the schema")) ())))
    result)
  )