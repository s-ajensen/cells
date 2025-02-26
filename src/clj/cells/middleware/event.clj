(ns cells.middleware.event
  (:require [cask.core :as cask]
            [cells.middleware.script :as script]))

(defn triggered? [state entity event {:keys [trigger scope] :as listener}]
  (case scope
    :self (trigger entity event)
    :* (trigger state entity event)))

;; TODO - refactor with script/apply-scripts
(defn apply-listeners
  ([event state [_id {:keys [listeners] :as entity}]]
   (apply-listeners event state listeners entity))
  ([event state listeners {:keys [id] :as entity}]
   (let [triggered? #(triggered? state entity event %)]
     (reduce (fn [state {:keys [scope next-state]}]
               (case scope
                 :self (assoc-in state [:entities id] (next-state entity event))
                 :* (next-state state entity event)))
             state (filter triggered? listeners)))))

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