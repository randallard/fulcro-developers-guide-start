(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [name age]}]
  (dom/li name (dom/span {:style {:fontStyle "italic"}} " (Age " age ")")))

(defsc Planet [this {:planet/keys [name esi-percent]}]
  (dom/p name (dom/span {:style {:fontStyle "italic"}} " (ESI " esi-percent "%)")))

(defsc Doodle [this {:doodle/keys [name url]}]
  (dom/p (dom/a {:href url :target "_blank"} name)))

(def ui-planet (comp/factory Planet))
(def ui-person (comp/factory Person))
(def ui-doodle (comp/factory Doodle))

(defsc PersonList [this {:person-list/keys [label people]}]
  (dom/div (dom/h3 label) (dom/ul (map ui-person people))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [this {:keys [ui/react-key]}]
  (let [ui-data {:person-list/label "People" :person-list/people
                 [{:person/name "Joe" :person/age 22}
                  {:person/name "Katch" :person/age 93}
                  {:person/name "Stank" :person/age 44}]}]
    (dom/div {:style {:fontFamily "sans-serif"}}
             (ui-person-list ui-data)
             (dom/h3 "Planets")
             (dom/ul
               (dom/li (ui-planet {:planet/name "Kepler-62 e" :planet/esi-percent 82}))
               (dom/li (ui-planet {:planet/name "Proxima Centauri b" :planet/esi-percent 87}))
               (dom/li (ui-planet {:planet/name "Ross 128 b" :planet/esi-percent 86})))
             (dom/h3 "Google Doodles")
             (dom/ul
               (dom/li (ui-doodle {:doodle/name "Fischinger"
                                   :doodle/url  "https://www.google.com/logos/doodles/2017/fischinger/fischinger17.9.html?hl=en"}))
               (dom/li (ui-doodle {:doodle/name "Great Union Day 2021"
                                   :doodle/url  "https://www.google.com/doodles/great-union-day-2021"}))
               (dom/li (ui-doodle {:doodle/name "Josephine Baker's 111th Birthday"
                                   :doodle/url  "https://www.google.com/doodles/josephine-bakers-111th-birthday"}))))))