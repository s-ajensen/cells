(ns cells.engine
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.render :as r]
            [clojure2d.core :as c2d]))

(def w 800)
(def h 600)

(deftype CellEngine [window]
  cask/Steppable
  (setup [this]
    {:tick 1
     :entities {(ccc/new-uuid)
                {:kind      :cell
                 :transform {:x 0 :y 0}
                 :velocity  {:x 1 :y 1}}}})
  (next-state [this state]
    (if-not (c2d/window-active? window)
      (System/exit 0)
      (reduce (fn [state middleware] (cask/next-state middleware state)) state
              [(->TransformMiddleware)
               (->ScriptMiddleware)])))
  cask/Renderable
  (render [this state]
    (let [canvas (c2d/canvas w h)]
      (c2d/with-canvas-> canvas
                         (r/render-state state))
      (c2d/replace-canvas window canvas)
      (c2d/repaint window))))