(ns app.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-person
  "Mutation: Delete the person with `:person/id` from the list with `:list/id`"
  [{list-id :person-list/id
    person-id :person/id}]
  (action [{:keys [state]}]
          (swap! state merge/remove-ident* [:person/id person-id] [:person-list/id list-id :person-list/people]))
  (remote [env] true))

(defmutation delete-planet
  "Mutation: Delete the planet with `id` from the list"
  [{list-id :planet-list/id
    planet-id :planet/id}]
  (action [{:keys [state]}]
          (swap! state merge/remove-ident* [:planet/id planet-id] [:planet-list/id list-id :planet-list/planets]))
  (remote [env] true))

(defmutation delete-site
  "Mutation: Delete the site with `id` from the list"
  [{list-id :site-list/id
    site-id :site/id}]
  (action [{:keys [state]}]
          (swap! state merge/remove-ident* [:site/id site-id] [:site-list/id list-id :site-list/sites]))
  (remote [env] true))
