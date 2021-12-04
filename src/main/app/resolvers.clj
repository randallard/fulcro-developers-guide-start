(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]))

(def people-table
  { 1 {:person/id 1 :person/name "Joe" :person/age 22}
    2 {:person/id 2 :person/name "Katch" :person/age 93}
    3 {:person/id 3 :person/name "Brandon" :person/age 40}
    4 {:person/id 4 :person/name "Stank" :person/age 44}
    5 {:person/id 5 :person/name "Phil" :person/age 70}
})

(def list-table
  {:dancers     {:list/id     :dancers
                 :list/label  "Dancers"
                 :list/people [1 2 3]}
   :not-dancers {:list/id     :not-dancers
                 :list/label  "Dancers"
                 :list/people [5 4]}
   :friends     {:list/id     :friends
                 :list/label  "Friends"
                 :list/people [1 2 3]}})

;; Given :person/id, this can generate the details of a person
(pc/defresolver person-resolver [env {:person/keys [id]}]
                {::pc/input  #{:person/id}
                 ::pc/output [:person/name :person/age]}
                (get people-table id))

;; Given a :list/id, this can generate a list label and the people
;; in that list (but just with their IDs)
(pc/defresolver list-resolver [env {:list/keys [id]}]
                {::pc/input  #{:list/id}
                 ::pc/output [:list/label {:list/people [:person/id]}]}
                (when-let [list (get list-table id)]
                  (assoc list
                    :list/people (mapv (fn [id] {:person/id id}) (:list/people list)))))

(pc/defresolver dancers-resolver [env input]
                {::pc/output [{:dancers [:list/id]}]}
                {:dancers {:list/id :dancers}})

(pc/defresolver not-dancers-resolver [env input]
                {::pc/output [{:not-dancers [:list/id]}]}
                {:not-dancers {:list/id :not-dancers}})

(pc/defresolver friends-resolver [env input]
                {::pc/output [{:friends [:list/id]}]}
                {:friends {:list/id :friends}})

(def resolvers [person-resolver list-resolver dancers-resolver not-dancers-resolver friends-resolver])