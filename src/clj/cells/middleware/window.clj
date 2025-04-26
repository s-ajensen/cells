(ns cells.middleware.window
  (:require [cells.entity.core :as entity]
            [cells.trigger :as trigger]
            [cells.middleware.event-poll :refer [->EventPollMiddleware]]
            [cask.core :as cask]))

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

(deftype WindowMiddleware [window-spec]
  cask/Steppable
  (setup [_this state]
    ((:init-fn! window-spec))
    (-> (update state :entities add-listeners)
        (assoc :renderer (:renderer window-spec))))
  (next-state [_this state]
    (cask/next-state (->EventPollMiddleware (:event-poller window-spec)) state)))