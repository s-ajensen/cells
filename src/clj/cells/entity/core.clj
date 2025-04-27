(ns cells.entity.core
  (:require [c3kit.apron.corec :as ccc]))

(defn ->entity [spec]
  (let [id (ccc/new-uuid)]
    (assoc spec :id id)))

(defn add-entity
  ([spec]
   (add-entity {} spec))
  ([entities spec]
   (let [{:keys [id] :as entity} (->entity spec)]
     (assoc entities id entity))))

(defn select-kind [entities kind]
  (select-keys entities (for [[k v] entities :when (= kind (:kind v))] k)))