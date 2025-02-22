(ns cells.main
  (:require [cask.core :as cask]
            [clojure2d.core :as c2d])
  (:import (cask.core GameEngine)))

(def w 800)
(def h 600)

(def window (c2d/show-window
              {:canvas      (c2d/canvas w h)
               :window-name "Game Window"
               :w           w
               :h           h
               :refresher   :onrepaint}))

(deftype CellEngine []
  GameEngine
  (setup [this]
    {:tick 1 :x 0 :y 0})
  (nextState [this {:keys [tick] :as state}]
    (if-not (c2d/window-active? window)
      (System/exit 0)
      (assoc state
        :tick (inc tick)
        :x (* 50 (Math/cos (* 0.1 tick)))
        :y (* 50 (Math/sin (* 0.1 tick))))))
  (render [this {:keys [x y]}]
    (let [canvas (c2d/canvas 800 600)]
      (c2d/with-canvas-> canvas
                         (c2d/set-background :white)
                         (c2d/set-color :black 100)
                         (c2d/translate 400 300)
                         (c2d/ellipse x y 20 20))
      (c2d/replace-canvas window canvas)
      (c2d/repaint window))))

(defn -main [& args]
  (cask/game-loop (CellEngine.) 16))