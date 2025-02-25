(ns cells.middleware.event-poll
  (:require [cask.core :as cask]
            [cells.middleware.event :as event]))

(defprotocol Pollable
  (poll-events [this]))

(deftype EventPollMiddleware [pollable]
  cask/Steppable
  (next-state [_this state]
    (reduce event/enqueue-event state (poll-events pollable))))