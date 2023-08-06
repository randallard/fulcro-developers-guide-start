(ns app.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defsc Detail [this {:detail/keys [id type year manufacturer breed likes] :as props}]
  {:query [:detail/id :detail/type :detail/year :detail/manufacturer :detail/breed :detail/likes]
   :ident :detail/id})

(defsc Thing [this {:thing/keys [id name year detail] :as props}]
  {:query [:thing/id :thing/name :thing/year {:thing/detail (comp/get-query Detail)}]
   :ident :thing/id})

(defsc ThingCategory [this {:thing-category/keys [id name things] :as props}]
  {:query [:thing-category/id :thing-category/name {:thing-category/things (comp/get-query Thing)}]
   :ident :thing-category/id}
  (dom/div
    (dom/h2 name)))

(def ui-thing-category (comp/factory ThingCategory {:keyfn :thing-category/id}))

(defsc MyThings [this {:root/keys [my-things]}]
       {:query [{:root/my-things (comp/get-query ThingCategory)}]}
  (dom/div
    (dom/h1 "My Things")
    (dom/p "I have " 1 " things")
    (ui-thing-category my-things)))

(defsc Person [this {:person/keys [id name] :as props}]
  {}
  (dom/div
    (dom/div "Name " name)))
(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc Sample [this {:keys [sample]}]
  {}
  (dom/div
    (ui-person sample)))

(defonce APP (app/fulcro-app))

(defn ^:export init []
      (app/mount! APP Sample "app"))


; go from the bottom up into the repl
(comment
  (app/schedule-render! APP)
  (reset! (::app/state-atom APP) {:my-things {:thing-category/id 1
                                              :thing-category/name "Vehicles"}})


  (app/schedule-render! APP)
  (reset! (::app/state-atom APP) {:sample {:person/id 1
                                           :person/name "Jill"}})


  (app/current-state APP)
  (swap! (::app/state-atom APP) update-in [:detail/id 1 :detail/likes] inc)
  (app/current-state APP)
  (swap! (::app/state-atom APP) assoc-in [:detail/id 1 :detail/likes] 1)
  (app/current-state APP)
  (swap! (::app/state-atom APP) assoc-in [:detail/id 1 :detail/manufacturer] "Honda")
  (app/current-state APP)
  (swap! (::app/state-atom APP) assoc-in [:detail/id 1 :detail/year] 2014)
  (app/current-state APP)
  (swap! (::app/state-atom APP) assoc-in [:thing/id 5 :thing/name] "Civic")
  (app/current-state APP)
  (swap! (::app/state-atom APP) assoc-in [:thing/id 2 :thing/name] "Switch")

  (app/current-state APP)
  ; detail: id type year manufacturer breed
  (merge/merge-component! APP Detail {:detail/id 4
                                      :detail/type "Video Game System"
                                      :detail/manufacturer "Nintendo"
                                      :detail/year 2023}
                          :append [ :thing/id 2 :thing/detail])
  (app/current-state APP)
  ; detail: id type year manufacturer breed
  (merge/merge-component! APP Detail {:detail/id 3
                                      :detail/type "Motorcycle"
                                      :detail/manufacturer "Harley-Davidson"
                                      :detail/year 1990}
                          :append [ :thing/id 3 :thing/detail])
  (app/current-state APP)
  ; detail: id type year manufacturer breed
  (merge/merge-component! APP Detail {:detail/id 2
                                      :detail/type "Cow"
                                      :detail/breed "Guernsey"}
                          :append [ :thing/id 4 :thing/detail])
  (app/current-state APP)
  ; detail: id type year manufacturer breed
  (merge/merge-component! APP Detail {:detail/id 1
                                     :detail/type "Car"}
                          :append [ :thing/id 5 :thing/detail])

  (app/current-state APP)
  (swap! (::app/state-atom APP) assoc-in [:thing/id 3 :thing/year] 1990)
  (swap! (::app/state-atom APP) assoc-in [:thing/id 1 :thing/year] 2023)
  (app/current-state APP)
  (swap! (::app/state-atom APP) assoc-in [:thing/id 5 :thing/year] 2014)

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