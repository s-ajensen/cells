(ns cells.middleware.event
  (:require [cask.core :as cask]
            [cells.middleware.script :as script]))

(defn apply-listeners [event state [_id {:keys [listeners] :as entity}]]
  (script/apply-scripts state (filter #(= event (:event %)) listeners) entity))

(defn- maybe-halt [state]
  (if (= :halt state)
    :halt))

(defn- halt-or-dequeue [state]
  (or (maybe-halt state) (update state :event-queue rest)))

(defn trigger-event [{:keys [entities] :as state} event]
  (let [apply-listeners (partial apply-listeners event)]
    (-> (reduce apply-listeners state entities)
        (halt-or-dequeue))))

(deftype EventMiddleware []
  cask/Steppable
  (next-state [_this {:keys [event-queue] :as state}]
    (reduce trigger-event state event-queue)))

(defn enqueue-event [state event]
  (if-not (:event-queue state)
    (assoc state :event-queue [event])
    (update state :event-queue conj event)))