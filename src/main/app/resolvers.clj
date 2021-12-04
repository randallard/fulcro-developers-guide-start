(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]))

(def people-table
  { 1 {:person/id 1 :person/name "Joe" :person/age 22}
    2 {:person/id 2 :person/name "Katch" :person/age 93}
    3 {:person/id 3 :person/name "Brandon" :person/age 40}
    4 {:person/id 4 :person/name "Stank" :person/age 44}
    5 {:person/id 5 :person/name "Phil" :person/age 70}})

(def sites-table
  {1 {:site/id 1 :site/name "Exercism.org" :site/url "https://exercism.org/tracks/clojure"}
   2 {:site/id 2 :site/name "Brave Clojure" :site/url "https://www.braveclojure.com/"}
   3 {:site/id 3 :site/name "Clojurians Slack" :site/url "https://clojurians.slack.com/"}})

(def list-table
  {:dancers     {:person-list/id     :dancers
                 :person-list/label  "Dancers"
                 :person-list/people [1 2 3]}
   :not-dancers {:person-list/id     :not-dancers
                 :person-list/label  "Not Dancers"
                 :person-list/people [5 4]}
   :friends     {:person-list/id     :friends
                 :person-list/label  "Friends"
                 :person-list/people [3 4 5]}
   :clojure-resources {:site-list/id   :clojure-resources
                 :site-list/label "Clojure Resources"}
                 :site-list/sites    [1 2 3]})

;; Given :person/id, this can generate the details of a person
(pc/defresolver person-resolver [env {:person/keys [id]}]
                {::pc/input  #{:person/id}
                 ::pc/output [:person/name :person/age]}
                (get people-table id))

;; Given a :person-list/id, this can generate a list label and the people
;; in that list (but just with their IDs)
(pc/defresolver list-resolver [env {:person-list/keys [id]}]
                {::pc/input  #{:person-list/id}
                 ::pc/output [:person-list/label {:person-list/people [:person/id]}]}
                (when-let [list (get list-table id)]
                  (assoc list
                    :person-list/people (mapv (fn [id] {:person/id id}) (:person-list/people list)))))

(pc/defresolver dancers-resolver [env input]
                {::pc/output [{:dancers [:person-list/id]}]}
                {:dancers {:person-list/id :dancers}})

(pc/defresolver not-dancers-resolver [env input]
                {::pc/output [{:not-dancers [:person-list/id]}]}
                {:not-dancers {:person-list/id :not-dancers}})

(pc/defresolver friends-resolver [env input]
                {::pc/output [{:friends [:person-list/id]}]}
                {:friends {:person-list/id :friends}})

(def resolvers [person-resolver list-resolver dancers-resolver not-dancers-resolver friends-resolver])