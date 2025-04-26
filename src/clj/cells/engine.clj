(ns cells.engine
  (:require [cask.core :as cask]))

(defn reduce-middlewares [f state middlewares]
  (reduce (fn [state middleware] (f middleware state)) state middlewares))

(deftype CellEngine [middlewares]
  cask/Steppable
  (setup [_this state]
    (reduce-middlewares cask/setup state middlewares))
  (next-state [_this state]
    (reduce-middlewares cask/next-state state middlewares))
  cask/Renderable
  (render [_this state]
    (cask/render (:renderer state) state)))