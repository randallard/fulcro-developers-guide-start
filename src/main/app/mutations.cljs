(ns app.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defmutation delete-thing
  "Mutation: Delete the thing with `:thing/id` from the list with `:list/id`"
  [{list-id   :list/id
    thing-id :thing/id}]
  (action [{:keys [state]}]
          (swap! state merge/remove-ident* [:thing/id thing-id] [:list/id list-id :list/things])))