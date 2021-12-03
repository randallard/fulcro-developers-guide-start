(ns app.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation delete-person
  "Mutation: Delete the person with `name` from the list"
  [{:keys [name]}] ; (1)
  (action [{:keys [state]}] ; (2)
          (let [path [:people :person-list/people]
                old-list (get-in @state path)
                new-list (vec (filter #(not= (:person/name %) name) old-list))]
            (swap! state assoc-in path new-list))))

(defmutation delete-planet
  "Mutation: Delete the planet with `name` from the list"
  [{:keys [name]}]
  (action [{:keys [state]}]
          (let [path [:planets :planet-list/planets]
                old-list (get-in @state path)
                new-list (vec (filter #(not= (:planet/name %) name) old-list))]
            (swap! state assoc-in path new-list))))

