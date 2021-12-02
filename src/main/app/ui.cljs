(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [name age]}]
  {:initial-state (fn [{:keys [name age] :as params}] {:person/name name :person/age age})}
  (dom/li name (dom/span {:style {:fontStyle "italic"}} " (Age " age ")")))

(defsc Planet [this {:planet/keys [name esi-percent]}]
  (dom/li name (dom/span {:style {:fontStyle "italic"}} " (ESI " esi-percent "%)")))

(defsc Doodle [this {:doodle/keys [name url]}]
  (dom/li (dom/a {:href url :target "_blank"} name)))

(def ui-person (comp/factory Person {:keyfn :person/name}))
(def ui-planet (comp/factory Planet))
(def ui-doodle (comp/factory Doodle))

(defsc PersonList [this {:person-list/keys [label people]}]
  {:initial-state (fn [{:keys [label]}]
                    {:person-list/label  label
                     :person-list/people [(comp/get-initial-state Person {:name "Joe" :age 22})
                                          (comp/get-initial-state Person {:name "Katch" :age 93})
                                          (comp/get-initial-state Person {:name "Stank" :age 44})]})}
  (dom/div (dom/h3 label) (dom/ul (map ui-person people))))

(defsc PlanetList [this {:planet-list/keys [label planets]}]
  (dom/div (dom/h3 label) (dom/ul (map ui-planet planets))))

(defsc DoodleList [this {:doodle-list/keys [label doodles]}]
  (dom/div (dom/h3 label) (dom/ul (map ui-doodle doodles))))

(def ui-person-list (comp/factory PersonList))
(def ui-planet-list (comp/factory PlanetList))
(def ui-doodle-list (comp/factory DoodleList))

(defsc Root [this {:keys [people]}]
  {:initial-state (fn [params] {:people (comp/get-initial-state PersonList {:label "People"})})}
  (let [ui-data {:planets {:planet-list/label "Planets" :planet-list/planets
                           [{:planet/name "Kepler-62 e" :planet/esi-percent 82}
                            {:planet/name "Proxima Centauri b" :planet/esi-percent 87}
                            {:planet/name "Ross 128 b" :planet/esi-percent 86}]}
                 :doodles {:doodle-list/label "Google Doodles" :doodle-list/doodles
                           [{:doodle/name "Fischinger"
                             :doodle/url  "https://www.google.com/logos/doodles/2017/fischinger/fischinger17.9.html?hl=en"}
                            {:doodle/name "Great Union Day 2021"
                             :doodle/url  "https://www.google.com/doodles/great-union-day-2021"}
                            {:doodle/name "Josephine Baker's 111th Birthday"
                             :doodle/url  "https://www.google.com/doodles/josephine-bakers-111th-birthday"}]}}]
    (dom/div {:style {:fontFamily "sans-serif"}}
             (ui-person-list people)
             (ui-planet-list (:planets ui-data))
             (ui-doodle-list (:doodles ui-data)))))