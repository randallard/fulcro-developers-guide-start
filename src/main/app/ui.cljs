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

(defsc Site [this {:site/keys [id name url] :as props} {:keys [onDelete]}]
  {:query [:site/id :site/name :site/url]
   :ident (fn [] [:site/id (:site/id props)])}
  (dom/li (dom/a {:href url :target "_blank"} name) " " (dom/button {:onClick #(onDelete name)} " Delete ")))

(def ui-person (comp/factory Person {:keyfn :person/id}))
(def ui-planet (comp/factory Planet {:keyfn :planet/id}))
(def ui-doodle (comp/factory Doodle {:keyfn :doodle/name}))
(def ui-site (comp/factory Site {:keyfn :site/name}))

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

(defsc SiteList [this {:site-list/keys [id label sites] :as props}]
  {:query [:site-list/id :site-list/label {:site-list/sites (comp/get-query Site)}]
   :ident (fn [] [:site-list/id (:site-list/id props)])}
  (let [delete-site (fn [site-id] (comp/transact! this [(api/delete-site {:site-list/id id :site/id site-id})]))]
    (dom/div (dom/h3 label) (dom/ul (map #(ui-site (comp/computed % {:onDelete delete-site})) sites)))))

(def ui-person-list (comp/factory PersonList))
(def ui-planet-list (comp/factory PlanetList))
(def ui-doodle-list (comp/factory DoodleList))
(def ui-site-list (comp/factory SiteList))

(defsc Root [this {:keys [friends dancers not-dancers habitable not-habitable clojure-resources ;doodles
                          ; clj-sites
                          ]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:dancers (comp/get-query PersonList)}
                   {:not-dancers (comp/get-query PersonList)}
                   {:habitable (comp/get-query PlanetList)}
                   {:not-habitable (comp/get-query PlanetList)}
                   {:clojure-resources (comp/get-query SiteList)}
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
           (when clojure-resources (ui-site-list clojure-resources))
           ;(when doodles (ui-doodle-list doodles))
           ;(ui-clj-site-list clj-sites)
           ))