(ns cells.engine
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.window :as window]))

(def w 800)
(def h 600)

(deftype CellEngine [window]
  cask/Steppable
  (setup [_this]
    {:tick 1
     :entities {(ccc/new-uuid)
                {:kind      :cell
                 :transform {:x 0 :y 0}
                 :velocity  {:x 1 :y 1}}}})
  (next-state [_this state]
    (reduce (fn [state middleware] (cask/next-state middleware state)) state
            [(->TransformMiddleware)
             (->ScriptMiddleware)
             (->EventMiddleware window)]))
  cask/Renderable
  (render [_this state]
    (window/render window state)))