(ns sexpr-project.examples)

(def ex_orders
  {:orders [
            {:number 99503
             :date "1999-10-20"

             :addresses
             [{:type "Shipping"
               :name "Ellen Adams"
               :street "123 Maple Street"
               :city "Mill Valley"
               :state "CA"
               :zip 10999
               :country "USA"}

              {:type "Billing"
               :name "Tai Yee"
               :street "8 Oak Avenue"
               :city "Old Town"
               :state "PA"
               :zip 95819
               :country "USA"}]

             :items
             [{:item "872-AA"
               :name "Lawnmower"
               :quantity 1
               :price 148.95
               :comment "comment"},

              {:item "926-AA"
               :name "Baby Monitor"
               :quantity 2
               :price 39.98
               :ship_date "1999-05-21"}]}]})

(def ex_vehicles
  {:vehicles [
              {:number "AE368UK"
               :manufacturer "Toyota"
               :color "Red"
               :loans [{:reoccuring "Monthly"
                        :owners ["Bob"]}]
               :tires {:model 123123
                       :size 23}
               :engine {:model 30065}}]})

(def ex_catalog
  {:catalog [
             {:book "bk101"
              :author "Gambardella, Matthew"
              :title "XML Developer's Guide"
              :genre "Computer"
              :price 44.95
              :publish_date "2000-10-01"
              :description "An in-depth look at creating applications with XML."}
             {:book "bk102"
              :author "Ralls, Kim"
              :title "Midnight Rain"
              :genre "Fantasy"
              :price 5.95
              :publish_date "2000-12-16"
              :description "A former architect battles corporate zombies, an evil sorceress, and her own childhood to become queen of the world."}
             {:book "bk103"
              :author "Corets, Eva"
              :title "Maeve Ascendant"
              :genre "Fantasy"
              :price 5.95
              :publish_date "2000-11-17"
              :description "After the collapse of a nanotechnology society in England, the young survivors lay the foundation for a new society."}]})