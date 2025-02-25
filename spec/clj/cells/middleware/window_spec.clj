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
  (left-click? [this] false)
  (right-click? [this] false))

(deftype ClosingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] true)
  (left-click? [this] false)
  (right-click? [this] false))

(deftype LeftClickingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] false)
  (left-click? [this] true)
  (right-click? [this] false))

(deftype LeftClickingAndClosingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] true)
  (left-click? [this] true)
  (right-click? [this] false))

(deftype RightClickingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] false)
  (left-click? [this] false)
  (right-click? [this] true))

; TODO - macro-ize this stuff ^

(describe "Window Middleware"

  (it "enqueues nothing with idle window"
    (should= {} (cask/next-state (sut/->WindowMiddleware (->IdleWindow)) {})))

  (it "enqueues window-close event"
    (should= (event/enqueue-event {} :window-close)
             (cask/next-state (sut/->WindowMiddleware (->ClosingWindow)) {})))

  (it "enqueues left-click event"
    (should= (event/enqueue-event {} :left-click)
             (cask/next-state (sut/->WindowMiddleware (->LeftClickingWindow)) {})))

  (it "enqueues right-click event"
    (should= (event/enqueue-event {} :right-click)
             (cask/next-state (sut/->WindowMiddleware (->RightClickingWindow)) {})))

  (it "enqueues window-close and left-click event"
    (should= (-> (event/enqueue-event {} :window-close) (event/enqueue-event :left-click))
             (cask/next-state (sut/->WindowMiddleware (->LeftClickingAndClosingWindow)) {}))))