(ns cells.module.transform
  (:require [cask.core :as cask]))

(defn update-transform [{:keys [transform velocity] :as entity}]
  (cond-> entity
          transform (update :transform #(merge-with + % velocity))))

(deftype TransformMiddleware []
  cask/Steppable
  (next-state [this state]
    (update state :entities (partial map update-transform))))