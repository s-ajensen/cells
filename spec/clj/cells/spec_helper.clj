(ns cells.spec-helper
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.engine]
            [cells.middleware.event-poll :as poll]
            [speclj.core :refer :all])
  (:import (cells.engine CellEngine)))

(defn position [x y]
  {:position {:x x :y y}})

(defn size [w h]
  {:size {:x w :y h}})

(defn transform
  ([x y w h]
   (merge (position x y)
          (size w h)))
  ([x y]
   (position x y)))

(deftype WindowRenderer []
  cask/Renderable
  (render [_this _state]
    ((stub :render))))

(deftype WindowPoller [events]
  poll/Pollable
  (poll-events [_this _state]
    events))

(defn ->window [window-events]
  {:renderer (->WindowRenderer)
   :event-poller (->WindowPoller window-events)})

(defn ->engine
  ([]
   (->engine []))
  ([window-events]
  (CellEngine. (->window window-events))))

(defn ->next
  ([state] (->next state []))
  ([state window-events]
   (cask/next-state (->engine window-events) state)))

(defn find-entity [state label]
  (ccc/ffilter #(= label (:label %)) (vals (:entities state))))