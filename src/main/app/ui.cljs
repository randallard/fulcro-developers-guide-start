(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [app.mutations :as api]))

(defsc Person [this {:person/keys [id name age] :as props} {:keys [onDelete]}]
  {:query [:person/id :person/name :person/age]
   :ident (fn [] [:person/id (:person/id props)])}
  (dom/li name (dom/span {:style {:fontStyle "italic"}} (str "  (Age " age ") "))
          (dom/button {:onClick #(onDelete id)} " Delete ")))

(defsc Planet [this {:planet/keys [id name esi-percent] :as props} {:keys [onDelete]}]
  {:query [:planet/id :planet/name :planet/esi-percent]
   :ident (fn [] [:planet/id (:planet/id props)])}
  (dom/li name (dom/span {:style {:fontStyle "italic"}} (str " (ESI " esi-percent "%) "))
          (dom/button {:onClick #(onDelete id)} " Delete ")))

(defsc Doodle [this {:doodle/keys [name url]} {:keys [onDelete]}]
  {:query [:doodle/name :doodle/url]
   :initial-state (fn [{:keys [name url] :as params }] {:doodle/name name :doodle/url url})}
  (dom/li (dom/a {:href url :target "_blank"} name) " " (dom/button {:onClick #(onDelete name)} " Delete ")))

(defsc ClojureSite [this {:clj-site/keys [name url]}]
  {:initial-state (fn [{:keys [name url] :as params}] {:clj-site/name name :clj-site/url url})}
  (dom/li (dom/a {:href url :target "_blank"} name)))

(def ui-person (comp/factory Person {:keyfn :person/id}))
(def ui-planet (comp/factory Planet {:keyfn :planet/id}))
(def ui-doodle (comp/factory Doodle {:keyfn :doodle/name}))
(def ui-clj-site (comp/factory ClojureSite {:keyfn :clj-site/name}))

(defsc PersonList [this {:person-list/keys [id label people] :as props}]
  {:query         [:person-list/id :person-list/label {:person-list/people (comp/get-query Person)}]
   :ident         (fn [] [:person-list/id (:person-list/id props)])}
  (let [delete-person (fn [person-id] (comp/transact! this [(api/delete-person {:person-list/id id :person/id person-id})]))]
    (dom/div (dom/h3 label) (dom/ul (map #(ui-person (comp/computed % {:onDelete delete-person})) people)))))

(defsc PlanetList [this {:planet-list/keys [id label planets] :as props}]
  {:query         [:planet-list/id :planet-list/label {:planet-list/planets (comp/get-query Planet)}]
   :ident         (fn [] [:planet-list/id (:planet-list/id props)])}
  (let [delete-planet (fn [planet-id] (comp/transact! this [(api/delete-planet {:planet-list/id id :planet/id planet-id})]))]
    (dom/div (dom/h3 label) (dom/ul (map #(ui-planet (comp/computed % {:onDelete delete-planet})) planets)))))

(defsc DoodleList [this {:doodle-list/keys [label doodles]}]
  {:query [:doodle-list/label {:doodle-list/doodles (comp/get-query Doodle)}]
   :initial-state (fn [{:keys [label]}]
                    {:doodle-list/label   label
                     :doodle-list/doodles [(comp/get-initial-state Doodle {:name "Fischinger"
                                                                           :url  "https://www.google.com/logos/doodles/2017/fischinger/fischinger17.9.html?hl=en"})
                                           (comp/get-initial-state Doodle {:name "Great Union Day 2021"
                                                                           :url  "https://www.google.com/doodles/great-union-day-2021"})
                                           (comp/get-initial-state Doodle {:name "Josephine Baker's 111th Birthday"
                                                                           :url  "https://www.google.com/doodles/josephine-bakers-111th-birthday"})]})}
  (let [delete-doodle (fn [name] (comp/transact! this [(api/delete-doodle {:name name})]))]
    (dom/div (dom/h3 label) (dom/ul
                              (map (fn [p] (ui-doodle (comp/computed p {:onDelete delete-doodle}))) doodles)))))

(defsc ClojureSiteList [this {:clj-site/keys [label clj-sites]}]
  {:initial-state (fn [{:keys [label]}]
                    {:clj-site/label label
                     :clj-site/clj-sites [(comp/get-initial-state ClojureSite {:name "Exercism.org"
                                                                           :url "https://exercism.org/tracks/clojure"})
                                      (comp/get-initial-state ClojureSite {:name "Brave Clojure"
                                                                           :url "https://www.braveclojure.com/"})
                                      (comp/get-initial-state ClojureSite {:name "Clojurians Slack"
                                                                           :url "https://clojurians.slack.com/"})]})}
  (dom/div (dom/h3 label) (dom/ul (map ui-clj-site clj-sites))))

(def ui-person-list (comp/factory PersonList))
(def ui-planet-list (comp/factory PlanetList))
(def ui-doodle-list (comp/factory DoodleList))
(def ui-clj-site-list (comp/factory ClojureSiteList))

(defsc Root [this {:keys [friends dancers not-dancers habitable not-habitable ;doodles
                          ; clj-sites
                          ]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:dancers (comp/get-query PersonList)}
                   {:not-dancers (comp/get-query PersonList)}
                   {:habitable (comp/get-query PlanetList)}
                   {:not-habitable (comp/get-query PlanetList)}
                   ;{:doodles (comp/get-query DoodleList)}
                   ]
   :initial-state {}}
  (dom/div {:style {:fontFamily "sans-serif"}}
           (when (not (or friends dancers not-dancers       ; habitable not-habitable doodles
                          ))
             (dom/h3 "Lists currently contain no data"))
           (when friends
             (ui-person-list friends))
           (when dancers
             (ui-person-list dancers))
           (when not-dancers (ui-person-list not-dancers))
           (when habitable (ui-planet-list habitable))
           (when not-habitable (ui-planet-list not-habitable))
           ;(when doodles (ui-doodle-list doodles))
           ;(ui-clj-site-list clj-sites)
           ))