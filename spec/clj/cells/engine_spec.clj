(ns cells.engine-spec
  (:require [cask.core :as cask]
            [cells.engine :as sut]
            [cells.window :as window]
            [speclj.core :refer :all]))

(deftype IdleWindow []
  window/Window
  (render [_this state])
  (window-close? [this] false))

(deftype ClosingWindow []
  window/Window
  (render [_this state])
  (window-close? [this] true))

(describe "Cells engine"

  (it "halts on window close event"
    (should= :halt (cask/next-state (sut/->CellEngine (->IdleWindow)) {:event-queue [:window-close]})))

  (it "halt closing window"
    (should= :halt (cask/next-state (sut/->CellEngine (->ClosingWindow)) nil)))

  )