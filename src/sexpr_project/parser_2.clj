(ns parser_2
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn parse-query [query]
  (re-seq #"[^\[\]/]+|\[.*?\]" query))

(defn format-entry [path key value]
  (let [new-path (str path "/" key)]
    (cond
      (map? value) (mapcat #(format-entry new-path %1 %2) (keys value) (vals value))
      (vector? value) (map #(format-entry new-path "*" %) value)
      :else [(str new-path "=\"" value "\"")])))

(defn process-data [data]
  (let [formatted-entries (flatten (map #(format-entry "" %1 %2) (keys data) (vals data)))]
    (println "Formatted Data:" (str "(" (clojure.string/join " " formatted-entries) ")"))))

(defn load-and-process-file [file-path]
  ;; Загрузка и обработка данных из файла
  (load-file file-path)
  (let [namespaces (ns-publics 'examples)]
    (doseq [[sym var] namespaces]
      (process-data (var-get var)))))

;; Пример
(let [file-path "examples.clj"] 
  (load-and-process-file file-path))
