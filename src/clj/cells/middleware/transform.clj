(ns cells.middleware.transform
  (:require [cask.core :as cask]))

(defn update-transform [{:keys [transform velocity] :as entity}]
  (let [position (:position transform)]
    (cond-> entity
            position (update-in [:transform :position] #(merge-with + % velocity)))))

(deftype TransformMiddleware []
  cask/Steppable
  (next-state [_this state]
    (update state :entities #(update-vals % update-transform))))