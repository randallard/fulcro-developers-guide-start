(ns app.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation delete-person
  "Mutation: Delete the person with `name` from the list with `list-name`"
  [{:keys [list-name name]}] ; (1)
  (action [{:keys [state]}] ; (2)
          (let [path [:people :people-list/people]
                old-list (get-in @state path)
                new-list (vec (filter #(not= (:person/name %) name) old-list))]
            (swap! state assoc-in path new-list))))