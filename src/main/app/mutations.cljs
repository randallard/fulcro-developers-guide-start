(ns app.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-person
  "Mutation: Delete the person with `:person/id` from the list with `:list/id`"
  [{list-id :person-list/id
    person-id :person/id}]
  (action [{:keys [state]}]
          (swap! state merge/remove-ident* [:person/id person-id] [:person-list/id list-id :person-list/people])))

(defmutation delete-planet
  "Mutation: Delete the planet with `name` from the list"
  [{:keys [name]}]
  (action [{:keys [state]}]
          (let [path [:planets :planet-list/planets]
                old-list (get-in @state path)
                new-list (vec (filter #(not= (:planet/name %) name) old-list))]
            (swap! state assoc-in path new-list))))

(defmutation delete-doodle
  "Mutation: Delete the doodle with `name` from the list"
  [{:keys [name]}]
  (action [{:keys [state]}]
          (let [path [:doodles :doodle-list/doodles]
                old-list (get-in @state path)
                new-list (vec (filter #(not= (:doodle/name %) name) old-list))]
            (swap! state assoc-in path new-list))))

