(ns cells.engine
  (:require [cask.core :as cask]
            [cells.state.core :as state]))

(deftype CellEngine [middlewares]
  cask/Steppable
  (setup [_this state]
    (state/reduce-setup state middlewares))
  (next-state [_this state]
    (state/reduce-next-state state middlewares))
  cask/Renderable
  (render [_this state]
    (cask/render (:renderer state) state)))