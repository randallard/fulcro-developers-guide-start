(ns app.deck
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

(defn get-deck [name]
  {:deck/name name
   :deck/cards [{ :card/id 1
                  :card/suit "Hearts"
                  :card/value 1
                  :card/name "Ace"}
                { :card/id 2
                 :card/suit "Hearts"
                 :card/value 2
                 :card/name "2"}
                { :card/id 3
                 :card/suit "Hearts"
                 :card/value 3
                 :card/name "3"}
                { :card/id 4
                 :card/suit "Hearts"
                 :card/value 4
                 :card/name "4"}
                { :card/id 5
                 :card/suit "Hearts"
                 :card/value 5
                 :card/name "5"}
                { :card/id 6
                 :card/suit "Hearts"
                 :card/value 6
                 :card/name "6"}
                { :card/id 7
                 :card/suit "Hearts"
                 :card/value 7
                 :card/name "7"}
                { :card/id 8
                 :card/suit "Hearts"
                 :card/value 8
                 :card/name "8"}
                { :card/id 9
                 :card/suit "Hearts"
                 :card/value 9
                 :card/name "9"}
                { :card/id 10
                 :card/suit "Hearts"
                 :card/value 10
                 :card/name "10"}
                { :card/id 11
                 :card/suit "Hearts"
                 :card/value 11
                 :card/name "Jack"}
                { :card/id 12
                 :card/suit "Hearts"
                 :card/value 12
                 :card/name "Queen"}
                { :card/id 13
                 :card/suit "Hearts"
                 :card/value 13
                 :card/name "King"}
                { :card/id 14
                 :card/suit "Clubs"
                 :card/value 1
                 :card/name "Ace"}
                { :card/id 15
                 :card/suit "Clubs"
                 :card/value 2
                 :card/name "2"}
                { :card/id 16
                 :card/suit "Clubs"
                 :card/value 3
                 :card/name "3"}
                { :card/id 17
                 :card/suit "Clubs"
                 :card/value 4
                 :card/name "4"}
                { :card/id 18
                 :card/suit "Clubs"
                 :card/value 5
                 :card/name "5"}
                { :card/id 19
                 :card/suit "Clubs"
                 :card/value 6
                 :card/name "6"}
                { :card/id 20
                 :card/suit "Clubs"
                 :card/value 7
                 :card/name "7"}
                { :card/id 21
                 :card/suit "Clubs"
                 :card/value 8
                 :card/name "8"}
                { :card/id 22
                 :card/suit "Clubs"
                 :card/value 9
                 :card/name "9"}
                { :card/id 23
                 :card/suit "Clubs"
                 :card/value 10
                 :card/name "10"}
                { :card/id 24
                 :card/suit "Clubs"
                 :card/value 11
                 :card/name "Jack"}
                { :card/id 25
                 :card/suit "Clubs"
                 :card/value 12
                 :card/name "Queen"}
                { :card/id 26
                 :card/suit "Clubs"
                 :card/value 13
                 :card/name "King"}
                { :card/id 27
                 :card/suit "Diamonds"
                 :card/value 1
                 :card/name "Ace"}
                { :card/id 28
                 :card/suit "Diamonds"
                 :card/value 2
                 :card/name "2"}
                { :card/id 29
                 :card/suit "Diamonds"
                 :card/value 3
                 :card/name "3"}
                { :card/id 30
                 :card/suit "Diamonds"
                 :card/value 4
                 :card/name "4"}
                { :card/id 31
                 :card/suit "Diamonds"
                 :card/value 5
                 :card/name "5"}
                { :card/id 32
                 :card/suit "Diamonds"
                 :card/value 6
                 :card/name "6"}
                { :card/id 33
                 :card/suit "Diamonds"
                 :card/value 7
                 :card/name "7"}
                { :card/id 34
                 :card/suit "Diamonds"
                 :card/value 8
                 :card/name "8"}
                { :card/id 35
                 :card/suit "Diamonds"
                 :card/value 9
                 :card/name "9"}
                { :card/id 36
                 :card/suit "Diamonds"
                 :card/value 10
                 :card/name "10"}
                { :card/id 37
                 :card/suit "Diamonds"
                 :card/value 11
                 :card/name "Jack"}
                { :card/id 38
                 :card/suit "Diamonds"
                 :card/value 12
                 :card/name "Queen"}
                { :card/id 39
                 :card/suit "Diamonds"
                 :card/value 13
                 :card/name "King"}
                { :card/id 40
                 :card/suit "Spades"
                 :card/value 1
                 :card/name "Ace"}
                { :card/id 41
                 :card/suit "Spades"
                 :card/value 2
                 :card/name "2"}
                { :card/id 42
                 :card/suit "Spades"
                 :card/value 3
                 :card/name "3"}
                { :card/id 43
                 :card/suit "Spades"
                 :card/value 4
                 :card/name "4"}
                { :card/id 44
                 :card/suit "Spades"
                 :card/value 5
                 :card/name "5"}
                { :card/id 45
                 :card/suit "Spades"
                 :card/value 6
                 :card/name "6"}
                { :card/id 46
                 :card/suit "Spades"
                 :card/value 7
                 :card/name "7"}
                { :card/id 47
                 :card/suit "Spades"
                 :card/value 8
                 :card/name "8"}
                { :card/id 48
                 :card/suit "Spades"
                 :card/value 9
                 :card/name "9"}
                { :card/id 49
                 :card/suit "Spades"
                 :card/value 10
                 :card/name "10"}
                { :card/id 50
                 :card/suit "Spades"
                 :card/value 11
                 :card/name "Jack"}
                { :card/id 51
                 :card/suit "Spades"
                 :card/value 12
                 :card/name "Queen"}
                { :card/id 52
                 :card/suit "Spades"
                 :card/value 13
                 :card/name "King"}
                ]})



(defsc Card [this {:card/keys [id suit value name] :as props}]
  {:query [:card/id :card/suit :card/value :card/name]
   :ident :card/id})

(defsc Deck [this {:deck/keys [id name cards] :as props}]
  {:query [:deck/id :deck/name {:deck/cards (comp/get-query Card)}]
   :ident :deck/id})

(comment
    (def deck-positions (map inc (range 52)))


    (map #(assoc % :deck/position ) deck-positions)
  )