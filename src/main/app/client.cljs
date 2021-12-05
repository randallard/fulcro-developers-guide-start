(ns app.client
  (:require
    [app.application :refer [app]]
    [app.ui :as ui]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.data-fetch :as df]))

(defn ^:export init []
  (app/mount! app ui/Root "app")
  (df/load! app :dancers ui/PersonList)
  (df/load! app :not-dancers ui/PersonList)
  (df/load! app :friends ui/PersonList)
  (df/load! app :habitable ui/PlanetList)
  (df/load! app :not-habitable ui/PlanetList)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  ;; re-mounting will cause forced UI refresh
  (app/mount! app ui/Root "app")
  ;; 3.3.0+ Make sure dynamic queries are refreshed
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))