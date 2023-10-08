(ns app.client
  (:require
    [app.application :refer [app]]
    [app.ui :as ui]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.application :as app]

    [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defn ^:export init []
  (app/mount! app ui/Root "app")
  (js/console.log "Loaded"))

(defn ^:export refresh []
  ;; re-mounting will cause forced UI refresh
  (app/mount! app ui/Root "app")
  ;; 3.3.0+ Make sure dynamic queries are refreshed
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))


(comment
  (app/current-state app)
  (merge/merge-component! app ui/Person #:person{:id 6 :name "Scorpia" :age 26})
  (merge/merge-component! app ui/PersonList #:list{:id :enemies :label "Enemies" :people [[:person/id 6] [:person/id 3]]})

  (app/current-state app)
  (merge/merge-component! app ui/Person #:person{:id 5 :name "Bo" :age 22})
  (merge/merge-component! app ui/PersonList #:list{:id :rebels :label "Rebels" :people [[:person/id 5] [:person/id 1]]})


  )