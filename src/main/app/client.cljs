(ns app.client
  (:require
    [app.table :as table]
    [app.deck :as deck]
    [app.player :as player]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
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

(defsc Car [this {:car/keys [id model] :as props}]
       {:query [:car/id :car/model]
        :ident :car/id
        :initial-state {:car/id :param/id
                        :car/model :param/model}}
       (dom/div
         "Model: " model))

(def ui-car (comp/factory Car {:kefn :car/id}))

(defsc Person [this {:person/keys [id name age cars] :as props}]
     {:query [:person/id :person/name :person/age {:person/cars (comp/get-query Car)}]
      :ident :person/id
      :initial-state {:person/id :param/id
                      :person/name :param/name
                      :person/age 0
                      :person/cars [{:id 40 :model "Leaf"}
                                    {:id 41 :model "Civic"}
                                    {:id 42 :model "RAV4"}]}}
  (dom/div
    (dom/div "Name: " name)
    (dom/div "Age: " age)
    (dom/button {:onClick #(comp/transact! this `[(make-older ~{:person/id id})])} "Make older")
    (dom/h3 "Cars")
    (dom/ul
      (map ui-car cars))))
(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc Sample [this {:root/keys [person]}]
  {:query [{:root/person (comp/get-query Person)}]
   :initial-state {:root/person {:id 1 :name "Bob"}}}
  (dom/div
    (ui-person person)))

(defonce APP (app/fulcro-app))

(defn ^:export init []
      (app/mount! APP Sample "app"))


(defmutation make-older [{:person/keys [id]}]
     (action [{:keys [state]}]
             (swap! state update-in [:person/id id :person/age] inc)))

; go from the bottom up into the repl
(comment
  #_#_(app/schedule-render! APP)
  (reset! (::app/state-atom APP) {:my-things {:thing-category/id 1
                                              :thing-category/name "Vehicles"}})

  (app/current-state APP)
  (comp/transact! APP  [(make-older {:person/id 2})])
  (make-older {:a 1})                                       ; returns itself as data
  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 2
                                      :person/name "Sally"
                                      :person/age 0})

  ;refresh browser after getting initial state
  (comp/get-initial-state Sample)


  (app/schedule-render! APP)
  (swap! (::app/state-atom APP) update-in [:person/id 3 :person/age] inc)

  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 3}
                          :replace [:root/person])

  (swap! (::app/state-atom APP) assoc-in [:person/id 3 :person/age] 42)

  (app/current-state APP)
  (merge/merge-component! APP Car {:car/id 24}
                          :append [:person/id 1 :person/cars])

  (app/current-state APP)
  (merge/merge-component! APP Car {:car/id 23}
                          :append [:person/id 3 :person/cars])

  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 3
                                      :person/name "Ted"
                                      :person/cars [{:car/id 24
                                                     :car/model "Tundra"}]}
                          :replace [:root/person])

  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 2
                                      :person/name "Sally"
                                      :person/cars [{:car/id 23
                                                     :car/model "RAV4"}]})

  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 1
                                      :person/name "Jill"
                                      :person/cars [{:car/id 22
                                                     :car/model "Civic"}]})
  (reset! (::app/state-atom APP) {})

  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 3
                                      :person/name "Ted"})
  (merge/merge-component! APP Person {:person/id 1
                                      :person/name "Jill"})
  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 2
                                      :person/name "Rhonda"})
  (app/current-state APP)
  (reset! (::app/state-atom APP) {})


  (app/schedule-render! APP)
  (reset! (::app/state-atom APP) {:sample {:person/id 1
                                           :person/name "Jill"
                                           :person/cars [{:car/id 22
                                                          :car/model "Civic"}]}})


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

  ; Normalization
  (reset! (::app/state-atom APP) {})
  (app/schedule-render! APP)
  (merge/merge-component! APP Person {:person/id 1
                                      :person/name "Joe"})
  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 2
                                      :person/name "Sally"})
  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id 3
                                      :person/name "Joe"})
  (app/current-state APP)


  ;  - adding the query and idents

  (reset! (::app/state-atom APP) {})
  (app/current-state APP)
  (merge/merge-component! APP Person {:person/id    1
                                      :person/name  "Joe"
                                      :person/contact {:contact/id 1
                                                       :contact/phone-number "5092209999"
                                                       :contact/email "Joe@testemail.com"}
                                      :person/secondary-contacts [{:contact/id 3
                                                                   :contact/phone-number "5092209997"}
                                                                  {:contact/id 4
                                                                   :contact/email "JoesMom@testemail.com"}]
                                      :person/cars  [{:car/id    22
                                                      :car/model "Escort"}]
                                      :person/books [{:book/id 1
                                                      :book/name "narnia 1"}
                                                     {:book/id 2
                                                      :book/name "narnia 2"}
                                                     {:book/id 3
                                                      :book/name "smithson"}]
                                      })
  (merge/merge-component! APP Person {:person/id    2
                                      :person/name  "Sally"
                                      :person/contact {:contact/id 2
                                                       :contact/phone-numbers "5092209998"
                                                       :contact/email "Sally@testemail.com"}
                                      :person/secondary-contacts [{:contact/id 5
                                                                   :contact/phone-number "5092209996"
                                                                   :contact/email "SallySecond@testemail.com"}
                                                                  {:contact/id 6
                                                                   :contact/phone-number "5092209995"}]
                                      :person/cars  [{:car/id    23
                                                      :car/model "Corolla"}]
                                      :person/books [{:book/id 1
                                                      :book/name "narnia 1"}
                                                     {:book/id 2
                                                      :book/name "narnia 2"}
                                                     {:book/id 4
                                                      :book/name "appson"}]
                                      })
  (app/current-state APP)

  (comp/get-ident Contact {:contact/id 5
                           :contact/phone-number "5092209996"})


  (deck/get-deck "poker")
  (reset! (::app/state-atom APP) {})
  (app/current-state APP)
  (merge/merge-component! APP deck/Deck {:deck/id 1
                                    :deck/name "poker"
                                    :deck/cards [ { :card/id 1
                                                    :card/suit "Hearts"
                                                    :card/value 1
                                                    :card/name "Ace"} ]})
  (merge/merge-component! APP deck/Deck (conj (deck/get-deck "poker") [:deck/id 1]))



  )