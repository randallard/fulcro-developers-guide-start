(ns app.things
  (:require
    [app.mutations :as api]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Thing [this {:thing/keys [id name] :as props} {:keys [onDelete]}]
  {:query         [:thing/id :thing/name] ; (2)
   :ident         (fn [] [:thing/id (:thing/id props)]) ; (1)
   :initial-state (fn [{:keys [id name] :as params}] {:thing/id id :thing/name name})} ; (3)
  (dom/li
    (dom/h5 (str name " ") (dom/button {:onClick #(onDelete id)} " delete ")))) ; (4)

(def ui-thing (comp/factory Thing {:keyfn :thing/id}))

(defsc ThingList [this {:list/keys [id label things] :as props}]
  {:query [:list/id :list/label {:list/things (comp/get-query Thing)}] ; (5)
   :ident (fn [] [:list/id (:list/id props)])
   :initial-state
   (fn [{:keys [id label]}]
     {:list/id     id
      :list/label  label
      :list/things (if (= id :farm)
                     [(comp/get-initial-state Thing {:id 1 :name "Tractor"})
                      (comp/get-initial-state Thing {:id 2 :name "Morna"})]
                     [(comp/get-initial-state Thing {:id 3 :name "Trumpet"})
                      (comp/get-initial-state Thing {:id 4 :name "Harley"})])})}
  (let [delete-thing (fn [thing-id] (comp/transact! this [(api/delete-thing {:list/id id :thing/id thing-id})]))] ; (4)
    (dom/div
      (dom/h4 label)
      (dom/ul
        (map #(ui-thing (comp/computed % {:onDelete delete-thing})) things)))))

(def ui-thing-list (comp/factory ThingList))