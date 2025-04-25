(ns cells.middleware.event-poll
  (:require [cask.core :as cask]
            [cells.middleware.event :as event]))

(defprotocol Pollable
  (poll-events [this state]))

(deftype EventPollMiddleware [pollable]
  cask/Steppable
  (setup [_this state] state)
  (next-state [_this state]
    (reduce event/enqueue-event state (poll-events pollable state))))