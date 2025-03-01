(ns cells.entity.core
  (:require [c3kit.apron.corec :as ccc]))

(defn ->entity [spec]
  (let [id (ccc/new-uuid)]
    (assoc spec :id id)))

(defn add-entity [entities spec]
  (let [{:keys [id] :as entity} (->entity spec)]
    (assoc entities id entity)))