(ns sexpr-project.html
  (:require [hiccup.core :as hiccup]
            [sexpr-project.examples :as examples]))

;; Объявляем функции заранее
(declare process-generic process-map process-vector process-scalar)

(defn- get-keys [data]
  (cond
    (map? data) (keys data)
    (vector? data) (range (count data))
    :else []))

(defn- process-map [data]
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

(defn- process-vector [data]
  [:div
   ;;[:strong "Vector:"]
   [:ul (for [item data]
          [:li (process-generic item)])]])

(defn- process-scalar [data]
  [:div
   ;;[:strong "Scalar:"]
   [:p (str data)]])

(defn- process-generic [data]
  (cond
    (map? data) (process-map data)
    (vector? data) (process-vector data)
    :else (process-scalar data)))

(defn- generate-and-print-html [example name]
  (let [html-content (hiccup/html (process-generic example))]
    (println (str "Generated HTML content for " name ":"))
    (println html-content)
    (println " ")))

(defn example []
  ;; Генерация и вывод HTML для ex_orders
  (generate-and-print-html examples/ex_orders "ex_orders")

  ;; Генерация и вывод HTML для ex_vehicles
  (generate-and-print-html examples/ex_vehicles "ex_vehicles")

  ;; Генерация и вывод HTML для ex_catalog
  (generate-and-print-html examples/ex_catalog "ex_catalog"))
