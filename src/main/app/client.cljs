(ns app.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defsc Thing [this {:thing/keys [id name] :as props}]
  {:query [:thing/id :thing/name]
   :ident :thing/id})

(defsc ThingCategory [this {:thing-category/keys [id name things] :as props}]
  {:query [:thing-category/id :thing-category/name {:thing-category/things (comp/get-query Thing)}]
   :ident :thing-category/id})


(defsc MyThings [this {:root/keys [thing-category]}]
       {:query [{:root/thing-category (comp/get-query ThingCategory)}]})

(defonce APP (app/fulcro-app))

(defn ^:export init []
      (app/mount! APP MyThings "app"))


; go from the bottom up into the repl
(comment
  (app/current-state APP)
  (merge/merge-component! APP Thing {:thing/id 5
                                     :thing/name "Honda Civic"}
                          :append [ :thing-category/id 1 :thing-category/things])
  (app/current-state APP)
  (merge/merge-component! APP Thing {:thing/id 4
                                     :thing/name "Morna"}
                          :append [ :thing-category/id 2 :thing-category/things])
  (app/current-state APP)
  (merge/merge-component! APP Thing {:thing/id 3
                                     :thing/name "Motorcycle"}
                          :append [ :thing-category/id 1 :thing-category/things])
  (app/current-state APP)
  (merge/merge-component! APP Thing {:thing/id 2
                                     :thing/name "Nintendo Switch"}
                          :append [ :thing-category/id 3 :thing-category/things])
  (app/current-state APP)
  (merge/merge-component! APP Thing {:thing/id 1
                                     :thing/name "Trumpet"}
                          :append [ :thing-category/id 3 :thing-category/things])
  (app/current-state APP)
  (merge/merge-component! APP ThingCategory {:thing-category/id 3
                                             :thing-category/name "Toys"})
  (merge/merge-component! APP ThingCategory {:thing-category/id 2
                                             :thing-category/name "Animals"})
  (merge/merge-component! APP ThingCategory {:thing-category/id 1
                                             :thing-category/name "Vehicles"})
  (app/current-state APP)
  (reset! (::app/state-atom APP) {})

  )