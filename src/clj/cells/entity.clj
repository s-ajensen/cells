(ns cells.entity
  (:require [c3kit.apron.corec :as ccc]))

(defn add-entity [entities spec]
  (let [id (ccc/new-uuid)]
    (assoc entities id (assoc spec :id id))))