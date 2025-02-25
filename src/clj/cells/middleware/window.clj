(ns cells.middleware.window
  (:require [cask.core :as cask]
            [cells.middleware.event :as event]
            [cells.window :as window]))

(def events {window/window-close? :window-close
             window/left-click?   :left-click
             window/right-click?  :right-click})

(defn maybe-enqueue-event [window state [event-fn event]]
  (if (event-fn window)
    (event/enqueue-event state event)
    state))

(deftype WindowMiddleware [window]
  cask/Steppable
  (next-state [_this state]
    (let [maybe-enqueue-event (partial maybe-enqueue-event window)]
      (reduce maybe-enqueue-event state events))))