(ns cells.engine-spec
  (:require [cask.core :as cask]
            [cells.spec-helper :as h]
            [cells.engine :as sut]
            [speclj.core :refer :all]))

(deftype NoSetupMiddleware []
  cask/Steppable
  (next-state [_this state] state))

(deftype IncMiddleware []
  cask/Steppable
  (setup [_this state] (inc state))
  (next-state [_this state] (inc state)))

(deftype DoubleMiddleware []
  cask/Steppable
  (setup [_this state] (* 2 state))
  (next-state [_this state] (* 2 state)))

(describe "Cells engine"
  (with-stubs)

  (context "setup"
    (it "reduces no middlewares"
      (should-be-nil (cask/setup (sut/->CellEngine nil []) nil))
      (should= :state (cask/setup (sut/->CellEngine nil []) :state)))

    ;; TODO - make this work?
    #_(it "no setup function"
      (should-be-nil (cask/setup (sut/->CellEngine nil [(->NoSetupMiddleware)]) nil)))

    (it "reduces a middleware"
      (should= 1 (cask/setup (sut/->CellEngine nil [(->IncMiddleware)]) 0))
      (should= 2 (cask/setup (sut/->CellEngine nil [(->IncMiddleware)]) 1))
      (should= 4 (cask/setup (sut/->CellEngine nil [(->DoubleMiddleware)]) 2)))

    (it "reduces multiple middlewares"
      (should= 4 (cask/setup (sut/->CellEngine nil [(->IncMiddleware)
                                                    (->DoubleMiddleware)]) 1))
      (should= 3 (cask/setup (sut/->CellEngine nil [(->DoubleMiddleware)
                                                    (->IncMiddleware)]) 1))
      (should= 5 (cask/setup (sut/->CellEngine nil [(->IncMiddleware)
                                                    (->DoubleMiddleware)
                                                    (->IncMiddleware)]) 1))
      (should= 8 (cask/setup (sut/->CellEngine nil [(->IncMiddleware)
                                                    (->DoubleMiddleware)
                                                    (->DoubleMiddleware)]) 1))))

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

  (it "renders state with window"
    (cask/render (sut/->CellEngine (h/->window []) []) :state)
    (should-have-invoked :render {:with [:state]})
    (cask/render (sut/->CellEngine (h/->window []) []) :other-state)
    (should-have-invoked :render {:with [:other-state]}))

  )