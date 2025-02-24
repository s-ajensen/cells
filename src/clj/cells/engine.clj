(ns cells.engine
  (:require [cask.core :as cask]
            [cells.module.script]
            [cells.module.transform]
            [cells.render :as r]
            [clojure2d.core :as c2d])
  (:import (cells.module.script ScriptMiddleware)
           (cells.module.transform TransformMiddleware)))

(def w 800)
(def h 600)

(deftype CellEngine [window]
  cask/Steppable
  (setup [this]
    {:tick 1 :entities [{:kind      :cell
                         :transform {:x 0 :y 0}
                         :velocity  {:x 1 :y 1}}]})
  (next-state [this state]
    (if-not (c2d/window-active? window)
      (System/exit 0)
      (reduce (fn [state middleware] (cask/next-state middleware state)) state
              [(TransformMiddleware.)
               (ScriptMiddleware.)])))
  cask/Renderable
  (render [this state]
    (let [canvas (c2d/canvas w h)]
      (c2d/with-canvas-> canvas
                         (r/render-state state))
      (c2d/replace-canvas window canvas)
      (c2d/repaint window))))