(ns cells.c2d
  (:require [cells.render :as render]
            [cells.middleware.window :as window]
            [clojure2d.core :as c2d]))

(def window (c2d/show-window
              {:canvas      (c2d/canvas window/w window/h)
               :window-name "Game Window"
               :w           window/w
               :h           window/h
               :refresher   :onrepaint
               }))

(def C2D-window-spec {:init-fn!     #(render/init! window)
                      :renderer     (render/->C2DRenderer window)
                      :event-poller (render/->C2DPoller window)})