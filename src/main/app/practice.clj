(:require
  [com.fulcrologic.fulcro.application :as app]
  [com.fulcrologic.fulcro.components :as comp :refer [defsc]])

(defsc Person [this props]
       {:query [:person/id :person/name]
        :ident :person/id
        :initial-state (fn [params] {:person/id (:id params)
                                     :person/name (:name params)})})



(defonce app (app/fulcro-app))

(defsc Root [this props]
       {:query [{:root/people (comp/get-query Person)}]
        :initial-state (fn [_] {:root/people [(comp/get-initial-state Person {:id 1 :name "Bob"})
                                              (comp/get-initial-state Person {:id 2 :name "Judy"})]})})

(app/mount! app Root :headless)

(app/current-state app)