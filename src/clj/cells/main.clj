(ns cells.main
  (:require [cask.core :as cask]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.attract :refer [->attract-middleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.middleware.window :refer [->WindowMiddleware]]
            [cells.c2d :refer [C2D-window-spec]])
  (:import (cells.engine CellEngine)))

(defn -main [& args]
  (let [engine (CellEngine. [;; TODO - fix attract-middleware
                             ;(->attract-middleware)
                             (->TransformMiddleware)
                             (->WindowMiddleware C2D-window-spec)
                             (->ScriptMiddleware)
                             (->EventMiddleware)])]
    (cask/game-loop engine 17)))