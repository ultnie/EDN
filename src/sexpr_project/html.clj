(ns sexpr-project.html
  (:require [hiccup.core :as hiccup]
            [sexpr-project.examples :as examples]))

;; Объявляем функции заранее
(declare process-generic process-map process-vector process-scalar)

(defn- get-keys 
  "Get keys from map or range of length of vector"
  [data]
  (cond
    (map? data) (keys data)
    (vector? data) (range (count data))
    :else []))

(defn- process-map 
  "Makes a table out of map"
  [data]
  (let [keys (get-keys data)]
    [:div
     ;;[:strong "Map:"]
     [:table {:border "1" :style "border: solid 1px lightgrey;"}
      [:thead
       [:tr
        (for [key keys]
          [:th key])]]
      [:tbody
       [:tr (for [key keys]
              [:td (process-generic (get data key))])]]]]))

(defn- process-vector 
  "Makes a table out of vector"
  [data]
  [:div
   ;;[:strong "Vector:"]
   [:ul (for [item data]
          [:li (process-generic item)])]])

(defn- process-scalar 
  "Makes a table out of rest datatypes that are not map or vector"
  [data]
  [:div
   ;;[:strong "Scalar:"]
   [:p (str data)]])

(defn- process-generic 
  "Checks [data] type and calls a function that creates a table from that type"
  [data]
  (cond
    (map? data) (process-map data)
    (vector? data) (process-vector data)
    :else (process-scalar data)))

(defn generate-html 
  "Generates html table for given EDN"
  [data]
  (hiccup/html (process-generic data))
  )

(defn print-html 
  "Prints html table"
  [html-str name]
    (println (str "Generated HTML content for " name ":"))
    (println html-str)
    (println " "))

(defn example []
  ;; Генерация и вывод HTML для ex_orders
  (print-html (generate-html examples/ex_orders) "ex_orders")

  ;; Генерация и вывод HTML для ex_vehicles
  (print-html (generate-html examples/ex_vehicles) "ex_vehicles")

  ;; Генерация и вывод HTML для ex_catalog
  (print-html (generate-html examples/ex_catalog) "ex_catalog"))
