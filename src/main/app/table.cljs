(ns app.table
  (:require
    [app.deck :as deck]
    [app.player :as player]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

(defsc Table [this {:table/keys [id game deck players] :as props}]
       {:query [:table/id :table/game {:table/deck (comp/get-query deck/Deck)} {:table/players (comp/get-query player/Player)}]
        :ident :table/id})
(defn get-players [number] (mapv #(player/get-player %) (range number)))

(comment
(get-players 4)
)