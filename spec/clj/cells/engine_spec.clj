(ns cells.engine-spec
  (:require [cask.core :as cask]
            [cells.spec-helper :as h]
            [cells.engine :as sut]
            [speclj.core :refer :all]))

(deftype IncMiddleware []
  cask/Steppable
  (next-state [_this state] (inc state)))

(deftype DoubleMiddleware []
  cask/Steppable
  (next-state [_this state] (* 2 state)))

(describe "Cells engine"

  (context "next-state"
    (it "reduces no middlewares"
      (should-be-nil (cask/next-state (sut/->CellEngine nil []) nil))
      (should= :state (cask/next-state (sut/->CellEngine nil []) :state)))

    (it "reduces a middleware"
      (should= 1 (cask/next-state (sut/->CellEngine nil [(->IncMiddleware)]) 0))
      (should= 2 (cask/next-state (sut/->CellEngine nil [(->IncMiddleware)]) 1))
      (should= 4 (cask/next-state (sut/->CellEngine nil [(->DoubleMiddleware)]) 2)))

    (it "reduces multiple middlewares"
      (should= 4 (cask/next-state (sut/->CellEngine nil [(->IncMiddleware)
                                                         (->DoubleMiddleware)]) 1))
      (should= 3 (cask/next-state (sut/->CellEngine nil [(->DoubleMiddleware)
                                                         (->IncMiddleware)]) 1))
      (should= 5 (cask/next-state (sut/->CellEngine nil [(->IncMiddleware)
                                                         (->DoubleMiddleware)
                                                         (->IncMiddleware)]) 1))
      (should= 8 (cask/next-state (sut/->CellEngine nil [(->IncMiddleware)
                                                         (->DoubleMiddleware)
                                                         (->DoubleMiddleware)]) 1))))

  )