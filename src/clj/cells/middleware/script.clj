(ns cells.middleware.script
  (:require [cask.core :as cask]))

(defn update-script [{:keys [scripts] :as entity}]
  (if scripts
    (reduce (fn [e s] ((:next-state s) e)) entity scripts)
    entity))

(deftype ScriptMiddleware []
  cask/Steppable
  (next-state [this state]
    (update state :entities #(update-vals % update-script))))