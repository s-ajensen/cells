(ns cells.middleware.script
  (:require [cask.core :as cask]))

(defn apply-scripts
  ([state [_id {:keys [scripts] :as entity}]]
   (apply-scripts state scripts entity))
  ([state scripts {:keys [id] :as entity}]
   (reduce (fn [state {:keys [scope next-state]}]
             (case scope
               :self (assoc-in state [:entities id] (next-state entity))
               :* (next-state state entity)))
           state scripts)))

(deftype ScriptMiddleware []
  cask/Steppable
  (setup [_this state] state)
  (next-state [_this {:keys [entities] :as state}]
    (reduce apply-scripts state entities)))