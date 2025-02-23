(ns cells.engine
  (:require [c3kit.apron.corec :as ccc]
            [cask.core]
            [cells.render :as r]
            [clojure2d.core :as c2d])
  (:import (cask.core GameEngine)))

(def w 800)
(def h 600)

(defn update-transform [{:keys [transform velocity] :as entity}]
  (cond-> entity
          transform (update :transform #(merge-with + % velocity))))

(defn update-transforms [state]
  (update state :entities (partial map update-transform)))

(defn update-script [{:keys [scripts] :as entity}]
  (if scripts
    (reduce (fn [e s] ((:next-state s) e)) entity scripts)
    entity))

(defn update-scripts [state]
  (update state :entities (partial map update-script)))

(defn next-state [state]
  (-> state
      update-transforms
      update-scripts))

(deftype CellEngine [window]
  GameEngine
  (setup [this]
    {:tick 1 :entities [{:kind      :cell
                         :transform {:x 0 :y 0}
                         :velocity  {:x 1 :y 1}}]})
  (nextState [this state]
    (if-not (c2d/window-active? window)
      (System/exit 0)
      (next-state state)))
  (render [this state]
    (let [canvas (c2d/canvas w h)]
      (c2d/with-canvas-> canvas
                         (r/render-state state))
      (c2d/replace-canvas window canvas)
      (c2d/repaint window))))