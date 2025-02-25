(ns cells.engine-spec
  (:require [cask.core :as cask]
            [cells.engine :as sut]
            [cells.entity :as entity]
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

(describe "Cells engine"

  #_(it "halts on window close event"
    (should= :halt (cask/next-state (sut/->CellEngine (->IdleWindow)) {:event-queue [:window-close]})))

  #_(it "halt closing window"
    (should= :halt (cask/next-state (sut/->CellEngine (->ClosingWindow)) nil)))

  )