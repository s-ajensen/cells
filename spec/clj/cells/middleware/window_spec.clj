(ns cells.middleware.window-spec
  (:require [cask.core :as cask]
            [cells.middleware.event :as event]
            [cells.middleware.window :as sut]
            [cells.window :as window]
            [speclj.core :refer :all]))

(deftype IdleWindow []
  window/Window
  (render [_this state])
  (window-close? [this] false)
  (left-click? [this] false))

(deftype ClosingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] true)
  (left-click? [this] false))

(deftype LeftClickingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] false)
  (left-click? [this] true))

(deftype LeftClickingAndClosingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] true)
  (left-click? [this] true))

(describe "Window Middleware"

  (it "enqueues nothing with idle window"
    (should= {} (cask/next-state (sut/->WindowMiddleware (->IdleWindow)) {})))

  (it "enqueues window-close event"
    (should= (event/enqueue-event {} :window-close)
             (cask/next-state (sut/->WindowMiddleware (->ClosingWindow)) {})))

  (it "enqueues left-click event"
    (should= (event/enqueue-event {} :left-click)
             (cask/next-state (sut/->WindowMiddleware (->LeftClickingWindow)) {})))

  (it "enqueues window-close and left-click event"
    (should= (-> (event/enqueue-event {} :window-close) (event/enqueue-event :left-click))
             (cask/next-state (sut/->WindowMiddleware (->LeftClickingAndClosingWindow)) {}))))