(ns sexpr_project.modify
  (:require [sexpr-project.parser_1 :as parser])
  (:require [sexpr-project.search :as search]))

(defn add-field 
  "Adds [to-add] to the path specified in the [query]. All vectors in the path should have indexes of maps inside that you want to add field to.
   To find the path if you only know some specific data in that map you can use sexpr_project.search/get-path"
  [query data to-add]
  (let [found_data (search/start-search query data)]
    ;(println (into [] (parser/prepare-for-assoc query)))
    (if (empty? found_data)
      (assoc-in data (into [] (parser/prepare-for-assoc query)) to-add)
      (throw (Exception. "That field already exists")))
       ))

(defn remove-field 
  "Removes field specified by the path in the [query] from [data]. All vectors in the path should have indexes of maps inside that you want to add field to.
To find the path if you only know some specific data in that map you can use sexpr_project.search/get-path"
  [query data]
  (let [found_data (search/start-search query data)
        q (parser/prepare-for-assoc query)]
    (if (empty? found_data)
      (throw (Exception. "Can't remove, field doesn't exist"))
      (update-in data (into [] (butlast q)) dissoc (last q)))))

(defn modify-field 
  "Changes value of field specified in [query] in [data] to [to-modify] value. Removes field specified by the path in the [query] from [data]. All vectors in the path should have indexes of maps inside that you want to add field to.
   To find the path if you only know some specific data in that map you can use sexpr_project.search/get-path and sexpr_project.search/path-to-str"
  [query data to-modify]
  (let [found_data (search/start-search query data)]
    (if (empty? found_data)
      (throw (Exception. "Can't modify, field doesn't exist"))
      (assoc-in data (into [] (parser/prepare-for-assoc query)) to-modify))))