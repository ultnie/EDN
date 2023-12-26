(ns sexpr_project.modify
  (:require [sexpr-project.parser_1 :as parser])
  (:require [sexpr-project.search :as search]))

(defn add-field [query data to-add]
  (let [found_data (search/start-search query data)]
    ;(println (into [] (parser/prepare-for-assoc query)))
    (if (empty? found_data)
      (assoc-in data (into [] (parser/prepare-for-assoc query)) to-add)
      "Can't add, that field already exists")))

(defn remove-field [query data]
  (let [found_data (search/start-search query data)
        q (parser/prepare-for-assoc query)]
    (if (empty? found_data)
      "Can't remove, field doesn't exists"
      (update-in data (into [] (butlast q)) dissoc (last q)))))

(defn modify-field [query data to-modify]
  (let [found_data (search/start-search query data)]
    (if (empty? found_data)
      "Can't modify, field doesn't exists"
      (assoc-in data (into [] (parser/prepare-for-assoc query)) to-modify))))