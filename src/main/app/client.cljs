(ns app.client
  (:require
    [app.deck :as deck]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defsc Car [this {:car/keys [id model] :as props}]
  {:query [:car/id :car/model]
   :ident :car/id})
(def ui-car (comp/factory Car {:keyfn :car/id}))

(defsc Contact [this {:contact/keys [id email phone-number] :as props}]
  {:query [:contact/id :contact/email :contact/phone-number]
   :ident :contact/id})
(def ui-contact (comp/factory Contact {:keyfn :contact/id}))

(defsc SecondaryContacts [this {:secondary-contacts/keys [id contacts]}]
  {:query [:secondary-contacts/id {:secondary-contacts/contacts (comp/get-query Contact)}]
   :ident :secondary-contacts/id})
(def ui-secondary-contacts (comp/factory SecondaryContacts))

(defsc Book [this {:book/keys [id name] :as props}]
  {:query [:book/id :book/name]
   :ident :book/id})
(def ui-book (comp/factory Book {:keyfn :book/id}))

(defsc Hand [this {:hand/keys [id cards] :as props}]
  {:query [:hand/id {:hand/cards (comp/get-query deck/Card)}]
   :ident :hand/id})

(defsc Person [this {:person/keys [id name cars books contact] :as props}]
  {:query [:person/id :person/name {:person/cars (comp/get-query Car)}
                    {:person/books (comp/get-query Book)}
                    {:person/contact (comp/get-query Contact)}
                    {:person/secondary-contacts (comp/get-query Contact)}]
   :ident :person/id})

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc Sample [this {:root/keys [person]}]
       {:query [{:root/person (comp/get-query Person)}]}
       (dom/div (ui-person person)))

(defonce APP (app/fulcro-app))

(defn ^:export init []
      (app/mount! APP Sample "app"))

(comment
  (keys APP)
  (-> APP (::app/state-atom) deref)
  (reset! (::app/state-atom APP) {:a 1})
  (app/schedule-render! APP)
  (reset! (::app/state-atom APP) {:sample {:person/id 1
                                           :person/name "Joe"}})

  (reset! (::app/state-atom APP) {:sample {:person/id 1
                                           :person/name "Joe"
                                           :person/cars [{:car/id 22
                                                          :car/model "Escort"}
                                                         {:car/id 23
                                                          :car/model "Corolla"}]}})

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


  (deck/get "poker")
  (reset! (::app/state-atom APP) {})
  (app/current-state APP)
  (merge/merge-component! APP deck/Deck {:deck/id 1
                                    :deck/name "poker"
                                    :deck/cards [ { :card/id 1
                                                    :card/suit "Hearts"
                                                    :card/value 1
                                                    :card/name "Ace"} ]})

  )