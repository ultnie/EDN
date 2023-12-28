(ns sexpr-project.validator
  (:require [clojure.set :as set])
  (:require [clojure.string])
  (:require [clojure.walk :as walk])
  )

;;(defn open-edn-from-string [x]
;;  (edn/read-string (pr-str x)))

(defn- compare-keys
  "Takes two lists of paths in schema and map, makes them into sets and looks for differences"
  [schema-keys data-keys]
  (set/difference (set data-keys) (set schema-keys)))

(defn- kvpaths-all
  "[m]: Find all paths in given map
   [prev m result]: prev - previous path, m - map where to look next, result - container to write results in"
  ([m] (kvpaths-all [] m ()))
  ([prev m result]
   (reduce-kv (fn [res k v] (if (associative? v)
                              (let [kp (conj prev k)]
                                (kvpaths-all kp v (conj res kp)))
                              (conj res (conj prev k))))
              result
              m)))

(defn validate-by-schema 
  "Takes schema in string form [schema-string] and validates that [data] corresponds to that schema"
  [schema-string data]
  (let [schema (read-string schema-string)
        schema-keys (map #(filter keyword? %) (kvpaths-all schema))
        data-keys (map #(filter keyword? %) (kvpaths-all data))
        extra-keys (compare-keys schema-keys data-keys)
        ]
    ;(println "Schema keys:" (kvpaths-all schema))
    ;(println "Data keys:" (kvpaths-all data))
    (empty? extra-keys)
    )) ; Возвращаем true, если отсутствуют лишние ключи