(ns cells.main
  (:require [cask.core :as cask]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.attract :refer [->attract-middleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.middleware.window :refer [->WindowMiddleware]]
            [cells.engine :refer [->CellEngine]]
            [cells.c2d :as c2d]))

(defn -main [& args]
  (let [engine (->CellEngine [(->attract-middleware)
                              (->TransformMiddleware)
                              (->WindowMiddleware @c2d/window-spec)
                              (->ScriptMiddleware)
                              (->EventMiddleware)])]
    (cask/game-loop engine 17)
    (System/exit 0)))