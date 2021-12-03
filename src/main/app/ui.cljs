(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [app.mutations :as api]))

(defsc Person [this {:person/keys [id name age] :as props} {:keys [onDelete]}]
  {:query [:person/id :person/name :person/age]
   :ident (fn [] [:person/id (:person/id props)])
   :initial-state (fn [{:keys [id name age] :as params}] {:person/id id :person/name name :person/age age})}
  (dom/li name (dom/span {:style {:fontStyle "italic"}} (str "  (Age " age ") "))
          (dom/button {:onClick #(onDelete id)} " Delete ")))

(defsc Planet [this {:planet/keys [name esi-percent]} {:keys [onDelete]}]
  {:query [:planet/name :planet/esi-percent]
   :initial-state (fn [{:keys [name esi-percent] :as params}] {:planet/name name :planet/esi-percent esi-percent})}
  (dom/li name (dom/span {:style {:fontStyle "italic"}} (str " (ESI " esi-percent "%) "))
          (dom/button {:onClick #(onDelete name)} " Delete ")))

(defsc Doodle [this {:doodle/keys [name url]} {:keys [onDelete]}]
  {:query [:doodle/name :doodle/url]
   :initial-state (fn [{:keys [name url] :as params }] {:doodle/name name :doodle/url url})}
  (dom/li (dom/a {:href url :target "_blank"} name) " " (dom/button {:onClick #(onDelete name)} " Delete ")))

(defsc ClojureSite [this {:clj-site/keys [name url]}]
  {:initial-state (fn [{:keys [name url] :as params}] {:clj-site/name name :clj-site/url url})}
  (dom/li (dom/a {:href url :target "_blank"} name)))

(def ui-person (comp/factory Person {:keyfn :person/id}))
(def ui-planet (comp/factory Planet {:keyfn :planet/name}))
(def ui-doodle (comp/factory Doodle {:keyfn :doodle/name}))
(def ui-clj-site (comp/factory ClojureSite {:keyfn :clj-site/name}))

(defsc PersonList [this {:person-list/keys [id label people] :as props}]
  {:query         [:person-list/id :person-list/label {:person-list/people (comp/get-query Person)}]
   :ident         (fn [] [:person-list/id (:person-list/id props)])
   :initial-state (fn [{:keys [id label]}]
                    {:person-list/id     id
                     :person-list/label  label
                     :person-list/people (if (= id :dancers) [(comp/get-initial-state Person {:id 1 :name "Joe" :age 22})
                                                              (comp/get-initial-state Person {:id 2 :name "Katch" :age 93})
                                                              (comp/get-initial-state Person {:id 3 :name "Brandon" :age 40 })]
                                                             [(comp/get-initial-state Person {:id 3 :name "Stank" :age 44})
                                                              (comp/get-initial-state Person {:id 4 :name "Phil" :age 70})])})}
  (let [delete-person (fn [person-id] (comp/transact! this [(api/delete-person {:person-list/id id :person/id person-id})]))]
    (dom/div (dom/h3 label) (dom/ul
                              (map #(ui-person (comp/computed % {:onDelete delete-person})) people)))))

(defsc PlanetList [this {:planet-list/keys [label planets]}]
  {:query [:planet-list/label {:planet-list/planets (comp/get-query Planet)}]
   :initial-state (fn [{:keys [label]}]
                    {:planet-list/label   label
                     :planet-list/planets [(comp/get-initial-state Planet {:name "Kepler-62 e" :esi-percent 82})
                                           (comp/get-initial-state Planet {:name "Proxima Centauri b" :esi-percent 87})
                                           (comp/get-initial-state Planet {:name "Ross 128 b" :esi-percent 86})]})}
  (let [delete-planet (fn [name] (comp/transact! this [(api/delete-planet {:name name})]))]
    (dom/div (dom/h3 label) (dom/ul
                              (map (fn [p] (ui-planet (comp/computed p {:onDelete delete-planet}))) planets)))))

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

(defsc Root [this {:keys [dancers not-dancers planets doodles
                          ; clj-sites
                          ]}]
  {:query         [{:dancers (comp/get-query PersonList)}
                   {:not-dancers (comp/get-query PersonList)}
                   {:planets (comp/get-query PlanetList)}
                   {:doodles (comp/get-query DoodleList)}]
   :initial-state (fn [params] {:dancers    (comp/get-initial-state PersonList {:id :dancers :label "Dancers"})
                                :not-dancers (comp/get-initial-state PersonList {:id :not-dancers :label "Not Dancers"})
                                :planets   (comp/get-initial-state PlanetList {:label "Planets"})
                                :doodles   (comp/get-initial-state DoodleList {:label "Google Doodles"})
                                :clj-sites (comp/get-initial-state ClojureSiteList {:label "Clojure Resources"})})}
  (dom/div {:style {:fontFamily "sans-serif"}}
           (ui-person-list dancers)
           (ui-person-list not-dancers)
           (ui-planet-list planets)
           (ui-doodle-list doodles)
           ;(ui-clj-site-list clj-sites)
           ))