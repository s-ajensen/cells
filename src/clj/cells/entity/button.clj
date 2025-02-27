(ns cells.entity.button
  (:require [cells.state.entity :as entity]
            [cells.trigger :as trigger]))

(defn point-in-transform? [{:keys [x y] :as point} transform]
  (let [tx (:x (:position transform))
        ty (:y (:position transform))
        tw (:x (:size transform))
        th (:y (:size transform))]
    (and (<= x (+ tx tw))
         (>= x tx)
         (<= y (+ ty th))
         (>= y ty))))

(defn call-if-event-in-transform [f state self event]
  (if (point-in-transform? (:position event) (:transform self))
    (f state self event)
    state))

(defn global-listener [global-callback]
  {:scope      :*
   :trigger    trigger/global-left-click?
   :next-state (partial call-if-event-in-transform global-callback)})

(defn add [state {:keys [label transform color global-callback] :as button}]
  (entity/add-entity state
    {:label     label
     :kind      :button
     :render?   true
     :transform transform
     :color     color
     :listeners [(global-listener global-callback)]}))