(ns cells.middleware.script
  (:require [cask.core :as cask]))

(defn apply-scripts [state [id {:keys [scripts] :as entity}]]
  (reduce (fn [state {:keys [scope next-state]}]
            (case scope
              :self (assoc-in state [:entities id] (next-state entity))
              :* (next-state state entity)))
          state scripts))

(deftype ScriptMiddleware []
  cask/Steppable
  (next-state [_this {:keys [entities] :as state}]
    (reduce apply-scripts state entities)))