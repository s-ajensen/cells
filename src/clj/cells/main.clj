(ns cells.main
  (:require [cask.core :as cask]
            [cells.render :as render]
            [cells.entity.window :as window]
            [clojure2d.core :as c2d]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.attract :refer [->AttractMiddleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event-poll :refer [->EventPollMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]])
  (:import (cells.engine CellEngine)))

(def red {:r 255 :g 0 :b 0 :a 255})
(def green {:r 0 :g 255 :b 0 :a 255})
(def blue {:r 0 :g 0 :b 255 :a 255})


(def window (c2d/show-window
              {:canvas      (c2d/canvas window/w window/h)
               :window-name "Game Window"
               :w           window/w
               :h           window/h
               ;:refresher   :onrepaint
               }))

(defn -main [& args]
  (render/init! window)
  (let [window {:renderer     (render/->C2DRenderer window)
                :event-poller (render/->C2DPoller window)}
        engine (CellEngine. window [(->AttractMiddleware {:attractions
                                                            {[green green] (fn [_attractor _attracted] 0.5)
                                                             [blue blue] (fn [_attractor _attracted] -0.5)
                                                             [red red] (fn [_attractor _attracted] -0.5)
                                                             [green red] (fn [_attractor _attracted] 0.5)
                                                             [red green] (fn [_attractor _attracted] -0.5)
                                                             [red blue] (fn [_attractor _attracted] 0.5)
                                                             [blue red] (fn [_attractor _attracted] -0.5)
                                                             [blue green] (fn [_attractor _attracted] 0.5)
                                                             [green blue] (fn [_attractor _attracted] -0.5)}})
                                    (->TransformMiddleware)
                                    (->ScriptMiddleware)
                                    (->EventPollMiddleware (:event-poller window))
                                    (->EventMiddleware)])]
    (cask/game-loop engine 17)
    (System/exit 0)))