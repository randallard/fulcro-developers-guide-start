(ns app.client
  (:require
    ["react-motion" :refer [Motion spring]]
    ["react-number-format" :default NumberFormat]
    [app.table :as table]
    [app.deck :as deck]
    [app.player :as player]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]
    ["react-dom/client" :as dom-client]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(def ui-number-format (interop/react-factory NumberFormat))

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

(def ui-motion (interop/react-factory Motion))

(defsc Block [this {:block/keys [name]} {:keys [top]}]
       {:query         [:block/id :block/name]
        :ident         :block/id
        :initial-state {:block/id   1
                        :block/name "A Block"}}
       (dom/div {:style {:position "relative"
                         :top      top}}
                (dom/div
                  (dom/label "Name?")
                  (dom/input {:value    name
                              :onChange #(m/set-string! this :block/name :event %)}))))

(def ui-block (comp/factory Block {:keyfn :id}))

(defsc Person [this {:person/keys [id name age cars] :as props}]
     {:query [:person/id :person/name :person/age {:person/cars (comp/get-query Car)}]
      :ident :person/id
      :initial-state {:person/id :param/id
                      :person/name :param/name
                      :person/age :param/age
                      :person/cars [{:id 40 :model "Leaf"}
                                    {:id 41 :model "Civic"}
                                    {:id 42 :model "RAV4"}]}
      :initLocalState (fn [this props]
                          {:onClick (fn [evt] (print "Click" props))})
      }
       (let [onClick (comp/get-state this :onClick)]
            (dom/div
              (dom/div
                (dom/label {:onClick onClick} "Name: ")
                name )
              (dom/div "Age: " age)
              (dom/button {:onClick #(comp/transact! this `[(make-older ~{:person/id id})])} "Make older")
              (dom/div
                       (dom/label "Some Dollars:")
                       #_(ui-number-format {:thousandSeparator true
                                          :prefix "$"}))
              #_#_(dom/h3 "Cars")
              (dom/ul
                (map ui-car cars)
                (dom/div
                  (dom/button {:onClick #(comp/transact! this `[(add-car ~{:person/id id})])} "Add Car"))))))
(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:person-list/keys [people] :as props}]
       {:query [{:person-list/people (comp/get-query Person)}]
        :ident (fn [_ _] [:component/id ::person-list])
        :initial-state {:person-list/people [{:id 1 :name "Jo" :age 43}
                                             {:id 2 :name "Sally" :age 23}]}}
       (dom/div
         (dom/h3 "People")
         (map ui-person people)))
(def ui-person-list (comp/factory PersonList))
(defsc Sample [this {:root/keys [people]}]
  {:query [{:root/people (comp/get-query PersonList)}]
   :initial-state {:root/people {}}}
  (dom/div
    (when people
          (ui-person-list people))
    ))

(defsc Demo [this {:keys [ui/slid? block]}]
       {:query         [:ui/slid? {:block (comp/get-query Block)}]
        :initial-state {:ui/slid? false :block {:id 1 :name "N"}}
        :ident         (fn [] [:control :demo])}
       (dom/div {:style {:overflow      "hidden"
                         :height        "150px"
                         :margin        "5px"
                         :padding       "5px"
                         :border        "1px solid black"
                         :borderRadius "10px"}}
                (dom/button {:onClick (fn [] (m/toggle! this :ui/slid?))} "Toggle")
                (ui-motion {:style {"y" (spring (if slid? 175 0))}}
                           (fn [p]
                               (let [y (comp/isoget p "y")]
                                    ; The binding wrapper ensures that internal fulcro bindings are held within the lambda
                                    (comp/with-parent-context this
                                                              (dom/div :.demo
                                                                       (ui-block (comp/computed block {:top y})))))))))

(def ui-demo (comp/factory Demo))
(defsc Root [this {:root/keys [demo] :as props}]
       {:query         [{:root/demo (comp/get-query Demo)}]
        :initial-state {:root/demo {}}}
       (ui-demo demo))

(defonce APP (-> (app/fulcro-app) (with-react18)))

(defn ^:export init []
      (app/mount! APP Root "app"))


(defmutation make-older [{:person/keys [id]}]
     (action [{:keys [state]}]
             (swap! state update-in [:person/id id :person/age] inc)))

(defmutation add-car [{:person/keys [id]}]
  (action [{:keys [state]}]
          (print "add car to person with id: " id)
          (let [new-key (inc (first (apply max-key val (:car/id (app/current-state APP)))))]
            (print "new key: " new-key)
            (merge/merge-component! APP Car {:car/id new-key
                                             :car/model (str "model-" new-key)}
                                    :append [:person/id id :person/cars]))))

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