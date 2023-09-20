(ns app.ui
  (:require
    [app.things :as things :refer [ui-thing-list ThingList]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc Root [this {:keys [farm fun]}]
  {:query         [{:farm (comp/get-query things/ThingList)}
                   {:fun  (comp/get-query ThingList)}]
   :initial-state (fn [params] {:farm (comp/get-initial-state ThingList {:id :farm :label "Farm"})
                                :fun (comp/get-initial-state ThingList {:id :fun :label "Fun"})})}
  (dom/div
    (dom/h2 "Our Stuff")
    (ui-thing-list farm)
    (ui-thing-list fun)))