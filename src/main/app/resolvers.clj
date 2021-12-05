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

(def planet-table
  { 1 {:planet/id 1 :planet/name "Earth" :planet/esi-percent 100}
    2 {:planet/id 2 :planet/name "Kepler-62 e" :planet/esi-percent 82}
    3 {:planet/id 3 :planet/name "Proxima Centauri b" :planet/esi-percent 87}
    4 {:planet/id 4 :planet/name "Ross 128 b" :planet/esi-percent 86}
    5 {:planet/id 5 :planet/name "Mercury" :planet/esi-percent 60}
    6 {:planet/id 6 :planet/name "Saturn" :planet/esi-percent 25}})

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
                 :person-list/people [3 4 5]}})

(def planet-list-table
  {:habitable     {:planet-list/id      :habitable
                   :planet-list/label   "Habitable?"
                   :planet-list/planets [1 2 3 4]}
   :not-habitable {:planet-list/id      :not-habitable
                   :planet-list/label   "Not Habitable"
                   :planet-list/planets [5 6]}})

(def site-list-table
  {:clojure-resources {:site-list/id    :clojure-resources
                       :site-list/label "Clojure Resources"
                       :site-list/sites [1 2 3]}})

;; Given :person/id, this can generate the details of a person
(pc/defresolver person-resolver [env {:person/keys [id]}]
                {::pc/input  #{:person/id}
                 ::pc/output [:person/name :person/age]}
                (get people-table id))

(pc/defresolver planet-resolver [env {:planet/keys [id]}]
                {::pc/input #{:planet/id}
                 ::pc/output [:planet/name :planet/esi-percent]}
                (get planet-table id))

;; Given a :person-list/id, this can generate a list label and the people
;; in that list (but just with their IDs)
(pc/defresolver list-resolver [env {:person-list/keys [id]}]
                {::pc/input  #{:person-list/id}
                 ::pc/output [:person-list/label {:person-list/people [:person/id]}]}
                (when-let [list (get list-table id)]
                  (assoc list
                    :person-list/people (mapv (fn [id] {:person/id id}) (:person-list/people list)))))

(pc/defresolver planet-list-resolver [env {:planet-list/keys [id]}]
                {::pc/input #{:planet-list/id}
                 ::pc/output [:planet-list/label {:planet-list/planets [:planet/id]}]}
                (when-let [list (get planet-list-table id)]
                  (assoc list
                    :planet-list/planets (mapv (fn [id] {:planet/id id}) (:planet-list/planets list)))))

(pc/defresolver habitable-resolver [env input]
                {::pc/output [{:habitable [:planet-list/id]}]}
                {:habitable {:planet-list/id :habitable}})

(pc/defresolver not-habitable-resolver [env input]
                {::pc/output [{:not-habitable [:planet-list/id]}]}
                {:not-habitable {:planet-list/id :not-habitable}})

(pc/defresolver dancers-resolver [env input]
                {::pc/output [{:dancers [:person-list/id]}]}
                {:dancers {:person-list/id :dancers}})

(pc/defresolver not-dancers-resolver [env input]
                {::pc/output [{:not-dancers [:person-list/id]}]}
                {:not-dancers {:person-list/id :not-dancers}})

(pc/defresolver friends-resolver [env input]
                {::pc/output [{:friends [:person-list/id]}]}
                {:friends {:person-list/id :friends}})

(def resolvers [person-resolver
                planet-resolver
                planet-list-resolver
                list-resolver
                habitable-resolver not-habitable-resolver
                dancers-resolver not-dancers-resolver friends-resolver])

(comment
  (app.parser/api-parser [{[:person/id 1] [:person/name]}])
  (app.parser/api-parser [{[:person-list/id :friends] [:person-list/id]}])
  (app.parser/api-parser [{[:person-list/id :friends] [:person-list/id {:person-list/people [:person/name]}]}])
  (app.parser/api-parser [{[:person-list/id :dancers] [:person-list/id {:person-list/people [:person/name]}]}])
  (app.parser/api-parser [{:friends [:person-list/id {:person-list/people [:person/name]}]}])
  (app.parser/api-parser [{[:planet/id 1] [:planet/name]}])
  (app.parser/api-parser [{[:planet-list/id :habitable] [:planet-list/id]}])
  (app.parser/api-parser [{[:planet-list/id :habitable] [:planet-list/id {:planet-list/planets [:planet/name]}]}])
  (app.parser/api-parser [{[:planet-list/id :habitable] [:planet-list/id :planet-list/label {:planet-list/planets [:planet/name]}]}])
  (app.parser/api-parser [{[:planet-list/id :not-habitable] [:planet-list/id :planet-list/label {:planet-list/planets [:planet/name]}]}])
  (app.parser/api-parser [{:habitable [:planet-list/id {:planet-list/planets [:planet/name]}]}])
  (app.parser/api-parser [{:not-habitable [:planet-list/id {:planet-list/planets [:planet/name]}]}])
  )