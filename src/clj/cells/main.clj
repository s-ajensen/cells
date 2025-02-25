(ns cells.main
  (:require [cask.core :as cask]
            [cells.engine :as engine]
            [cells.render :as render]
            [clojure2d.core :as c2d])
  (:import (cells.engine CellEngine)))

(def window (c2d/show-window
              {:canvas      (c2d/canvas engine/w engine/h)
               :window-name "Game Window"
               :w           engine/w
               :h           engine/h
               ;:refresher   :onrepaint
               }))

(defn -main [& args]
  (cask/game-loop (CellEngine. (render/->C2DWindow window)) 17)
  (System/exit 0))