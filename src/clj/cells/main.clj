(ns cells.main
  (:require [cask.core :as cask]
            [cells.render :as render]
            [cells.middleware.window :as window]
            [clojure2d.core :as c2d]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.attract :refer [->attract-middleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.middleware.window :refer [->WindowMiddleware]])
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
        engine (CellEngine. window [;; TODO - fix attract-middleware
                                    ;(->attract-middleware)
                                    (->TransformMiddleware)
                                    (->WindowMiddleware window)
                                    (->ScriptMiddleware)
                                    (->EventMiddleware)])]
    (cask/game-loop engine 17)))