(ns sexpr-project.validator
  (:require [clojure.set :as set])
  (:require [clojure.string])
  (:require [clojure.walk :as walk])
  )

;;(defn open-edn-from-string [x]
;;  (edn/read-string (pr-str x)))

(defn compare-keys [schema-keys data-keys]
(set/difference (set data-keys) (set schema-keys)))

(defn kvpaths-all
  ([m] (kvpaths-all [] m ()))
  ([prev m result]
   (reduce-kv (fn [res k v] (if (associative? v)
                              (let [kp (conj prev k)]
                                (kvpaths-all kp v (conj res kp)))
                              (conj res (conj prev k))))
              result
              m)))

(defn validate-schema [schema-string data]
  (let [schema (read-string schema-string)
        schema-keys (map #(filter keyword? %) (kvpaths-all schema))
        data-keys (map #(filter keyword? %) (kvpaths-all data))
        extra-keys (compare-keys schema-keys data-keys)
        ]
    ;(println "Schema keys:" data-keys)
    ;(println "Data keys:" schema-keys)
    (empty? extra-keys)
    )) ; Возвращаем true, если отсутствуют лишние ключи