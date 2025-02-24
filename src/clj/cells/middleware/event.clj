(ns cells.middleware.event
  (:require [cask.core :as cask]
            [cells.window :as window]))

(defn maybe-close-window [{:keys [event-queue] :as state}]
  (if (= :window-close (first event-queue))
    :halt
    state))

(defn poll-window-events [state window]
  (if (window/window-close? window)
    (update state :event-queue conj :window-close)
    state))

(deftype EventMiddleware [window]
  cask/Steppable
  (next-state [this state]
    (-> (poll-window-events state window)
        (maybe-close-window))))