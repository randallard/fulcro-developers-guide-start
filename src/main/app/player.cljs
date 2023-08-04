(ns app.player
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

(defsc Player [this {:player/keys [id name] :as props}]
       {:query [:player/id :player/name]
        :ident :player/id})
(def names ["One","Two","Three","Four","Five","Six","Seven","Eight"])

(defn get-name [] (rand-nth names))
(defn get-player [id] {:player/id id
                       :player/name (get-name)})

(comment
  (in-ns 'app.player)
  (get-name)
  (get-player 1)
  )