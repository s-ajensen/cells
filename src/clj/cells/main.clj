(ns cells.main
  (:require [cask.core :as cask]
            [cells.render :as render]
            [cells.state.window :as window]
            [clojure2d.core :as c2d])
  (:import (cells.engine CellEngine)))

(def window (c2d/show-window
              {:canvas      (c2d/canvas window/w window/h)
               :window-name "Game Window"
               :w           window/w
               :h           window/h
               :refresher   :onrepaint
               }))

(defn -main [& args]
  (render/init! window)
  (let [window {:renderer     (render/->C2DRenderer window)
                :event-poller (render/->C2DPoller window)}
        engine (CellEngine. window)]
    (cask/game-loop engine 17)
    (System/exit 0)))