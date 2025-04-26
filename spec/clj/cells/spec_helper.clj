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
  (render [_this state]
    ((stub :render) state)))

(deftype WindowPoller [events]
  poll/Pollable
  (poll-events [_this _state]
    events))

(defn ->window-spec [window-events]
  {:init-fn! (stub :window-init)
   :renderer (->WindowRenderer)
   :event-poller (->WindowPoller window-events)})

(defn ->engine [middlewares]
  (CellEngine. middlewares))

(defn ->next [state middlewares]
  (cask/next-state (->engine middlewares) state))

(defn find-entity [state label]
  (ccc/ffilter #(= label (:label %)) (vals (:entities state))))