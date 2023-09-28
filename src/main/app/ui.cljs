(ns app.ui
  (:require
    [app.mutations :as api]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [id name age] :as props} {:keys [onDelete]}]
  {:query         [:person/id :person/name :person/age] ; (2)
   :ident         (fn [] [:person/id (:person/id props)]) ; (1)
   :initial-state (fn [{:keys [id name age] :as params}] {:person/id id :person/name name :person/age age})} ; (3)
  (dom/li
    (dom/h5 (str name " (age: " age ") ") (dom/button {:onClick #(onDelete id)} " delete ")))) ; (4)

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:list/keys [id label people] :as props}]
  {:query [:list/id :list/label {:list/people (comp/get-query Person)}] ; (5)
   :ident (fn [] [:list/id (:list/id props)])
   :initial-state
   (fn [{:keys [id label]}]
     {:list/id     id
      :list/label  label
      :list/people (cond (= id :friends)
                     [(comp/get-initial-state Person {:id 1 :name "Sally" :age 32})
                      (comp/get-initial-state Person {:id 2 :name "Joe" :age 22})]
                         (= id :enemies)
                     [(comp/get-initial-state Person {:id 3 :name "Fred" :age 11})
                      (comp/get-initial-state Person {:id 4 :name "Bobby" :age 55})]
                         (= id :rebels)
                     [(comp/get-initial-state Person {:id 1 :name "Sally" :age 32})
                      (comp/get-initial-state Person {:id 5 :name "Bo" :age 22})])})}
  (let [delete-person (fn [person-id] (comp/transact! this [(api/delete-person {:list/id id :person/id person-id})]))] ; (4)
    (dom/div
      (dom/h4 label)
      (dom/ul
        (map #(ui-person (comp/computed % {:onDelete delete-person})) people)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [this {:keys [friends enemies rebels]}]
  {:query         [{:rebels (comp/get-query PersonList)}
                   {:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state (fn [params] {:friends (comp/get-initial-state PersonList {:id :friends :label "Friends"})
                                :enemies (comp/get-initial-state PersonList {:id :enemies :label "Enemies"})
                                :rebels (comp/get-initial-state PersonList {:id :rebels :label "Rebels"})})}
  (dom/div
    (ui-person-list rebels)
    (ui-person-list friends)
    (ui-person-list enemies)))