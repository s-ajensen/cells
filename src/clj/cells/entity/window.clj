(ns cells.entity.window
  (:require [cells.state.entity :as entity]
            [cells.trigger :as trigger]))

(def w 800)
(def h 600)

(def listener
  {:kind  :headless-listener
   :label "base-listeners"
   :listeners
   [{:scope      :*
     :trigger    trigger/global-window-close?
     :next-state (constantly :halt)}
    {:scope      :*
     :trigger    trigger/global-left-click?
     :next-state (fn [state self event] (prn "left") state)}
    {:scope      :*
     :trigger    trigger/global-right-click?
     :next-state (fn [state self event] (prn "right") state)}]})

(defn add-listeners [entities]
  (entity/add-entity entities listener))