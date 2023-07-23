(ns app.client
  (:require
    [app.application :refer [app]]
    [app.ui :as ui]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.data-fetch :as df]))


(defn ^:export init
      "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
      []
      (app/mount! app ui/Root "app")
      (df/load! app :friends ui/PersonList)
      (df/load! app :enemies ui/PersonList)
      (js/console.log "Loaded"))

(defn ^:export refresh
      "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
      []
      ;; re-mounting will cause forced UI refresh, update internals, etc.
      (app/mount! app ui/Root "app")
      ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
      (comp/refresh-dynamic-queries! app)
      (js/console.log "Hot reload"))
