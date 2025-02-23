(ns cells.main
  (:require [cask.core :as cask]
            [cells.render :as r]
            [clojure2d.core :as c2d])
  (:import (cask.core GameEngine)))

(def w 800)
(def h 600)

(def back-buffer (c2d/canvas w h))
(def front-buffer (c2d/canvas w h))

(def window (c2d/show-window
              {:canvas      front-buffer
               :window-name "Game Window"
               :w           w
               :h           h
               :refresher   :onrepaint}))

(deftype CellEngine []
  GameEngine
  (setup [this]
    {:tick 1 :entities [{:kind      :cell
                         :transform {:x 0 :y 0}}]})
  (nextState [this {:keys [tick entities] :as state}]
    (if-not (c2d/window-active? window)
      (System/exit 0)
      (let [[cell] entities]
        (-> state
            (update :tick inc)
            (assoc :entities [(-> cell
                                  (assoc-in [:transform :x] (* 50 (Math/cos (* 0.1 tick))))
                                  (assoc-in [:transform :y] (* 50 (Math/sin (* 0.1 tick)))))])))))
  (render [this state]
    (let [canvas (c2d/canvas w h)]
      (c2d/with-canvas-> canvas
                         (r/render-state state))
      (c2d/replace-canvas window canvas)
      (c2d/repaint window))))

(defn -main [& args]
  (cask/game-loop (CellEngine.) 17))