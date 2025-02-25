(ns cells.window)

(defprotocol Window
  (render [this state])
  (window-close? [this])
  (left-click? [this])
  (right-click? [this]))