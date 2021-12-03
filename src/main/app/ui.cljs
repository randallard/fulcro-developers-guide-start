(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [name age]}]
  {:initial-state (fn [{:keys [name age] :as params}] {:person/name name :person/age age})}
  (dom/li name (dom/span {:style {:fontStyle "italic"}} " (Age " age ")")))

(defsc Planet [this {:planet/keys [name esi-percent]}]
  {:initial-state (fn [{:keys [name esi-percent] :as params}] {:planet/name name :planet/esi-percent esi-percent})}
  (dom/li name (dom/span {:style {:fontStyle "italic"}} " (ESI " esi-percent "%)")))

(defsc Doodle [this {:doodle/keys [name url]}]
  {:initial-state (fn [{:keys [name url] :as params}] {:doodle/name name :doodle/url url})}
  (dom/li (dom/a {:href url :target "_blank"} name)))

(def ui-person (comp/factory Person {:keyfn :person/name}))
(def ui-planet (comp/factory Planet {:keyfn :planet/name}))
(def ui-doodle (comp/factory Doodle {:keyfn :planet/name}))

(defsc PersonList [this {:person-list/keys [label people]}]
  {:initial-state (fn [{:keys [label]}]
                    {:person-list/label  label
                     :person-list/people [(comp/get-initial-state Person {:name "Joe" :age 22})
                                          (comp/get-initial-state Person {:name "Katch" :age 93})
                                          (comp/get-initial-state Person {:name "Stank" :age 44})]})}
  (dom/div (dom/h3 label) (dom/ul (map ui-person people))))

(defsc PlanetList [this {:planet-list/keys [label planets]}]
  {:initial-state (fn [{:keys [label]}]
                    {:planet-list/label   label
                     :planet-list/planets [(comp/get-initial-state Planet {:name "Kepler-62 e" :esi-percent 82})
                                           (comp/get-initial-state Planet {:name "Proxima Centauri b" :esi-percent 87})
                                           (comp/get-initial-state Planet {:name "Ross 128 b" :esi-percent 86})]})}
  (dom/div (dom/h3 label) (dom/ul (map ui-planet planets))))

(defsc DoodleList [this {:doodle-list/keys [label doodles]}]
  {:initial-state (fn [{:keys [label]}]
                    {:doodle-list/label   label
                     :doodle-list/doodles [(comp/get-initial-state Doodle {:name "Fischinger"
                                                                           :url  "https://www.google.com/logos/doodles/2017/fischinger/fischinger17.9.html?hl=en"})
                                           (comp/get-initial-state Doodle {:name "Great Union Day 2021"
                                                                           :url  "https://www.google.com/doodles/great-union-day-2021"})
                                           (comp/get-initial-state Doodle {:name "Josephine Baker's 111th Birthday"
                                                                           :url  "https://www.google.com/doodles/josephine-bakers-111th-birthday"})]})}
  (dom/div (dom/h3 label) (dom/ul (map ui-doodle doodles))))

(def ui-person-list (comp/factory PersonList))
(def ui-planet-list (comp/factory PlanetList))
(def ui-doodle-list (comp/factory DoodleList))

(defsc Root [this {:keys [people planets doodles]}]
  {:initial-state (fn [params] {:people (comp/get-initial-state PersonList {:label "People"})
                                :planets (comp/get-initial-state PlanetList {:label "Planets"})
                                :doodles (comp/get-initial-state DoodleList {:label "Google Doodles"})})}
  (dom/div {:style {:fontFamily "sans-serif"}}
           (ui-person-list people)
           (ui-planet-list planets)
           (ui-doodle-list doodles)))