(ns cells.engine-spec
  (:require [cask.core :as cask]
            [cells.spec-helper :as h]
            [cells.engine :as sut]
            [speclj.core :refer :all]))

(deftype NoSetupMiddleware []
  cask/Steppable
  (next-state [_this state] state))

(deftype ArithmeticMiddleware []
  cask/Steppable
  (setup [_this state] (dec state))
  (next-state [_this state] (inc state)))

(deftype MultiplicativeMiddleware []
  cask/Steppable
  (setup [_this state] (quot state 2))
  (next-state [_this state] (* state 2)))

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
      (should= 1 (cask/setup (sut/->CellEngine nil [(->ArithmeticMiddleware)]) 2))
      (should= 2 (cask/setup (sut/->CellEngine nil [(->ArithmeticMiddleware)]) 3))
      (should= 4 (cask/setup (sut/->CellEngine nil [(->MultiplicativeMiddleware)]) 8)))

    (it "reduces multiple middlewares"
      (should= 4 (cask/setup (sut/->CellEngine nil [(->ArithmeticMiddleware)
                                                    (->MultiplicativeMiddleware)]) 9))
      (should= 3 (cask/setup (sut/->CellEngine nil [(->MultiplicativeMiddleware)
                                                    (->ArithmeticMiddleware)]) 9))
      (should= 5 (cask/setup (sut/->CellEngine nil [(->ArithmeticMiddleware)
                                                    (->MultiplicativeMiddleware)
                                                    (->ArithmeticMiddleware)]) 13))
      (should= 3 (cask/setup (sut/->CellEngine nil [(->ArithmeticMiddleware)
                                                    (->MultiplicativeMiddleware)
                                                    (->MultiplicativeMiddleware)]) 13))))

  (context "next-state"
    (it "reduces no middlewares"
      (should-be-nil (cask/next-state (sut/->CellEngine nil []) nil))
      (should= :state (cask/next-state (sut/->CellEngine nil []) :state)))

    (it "reduces a middleware"
      (should= 1 (cask/next-state (sut/->CellEngine nil [(->ArithmeticMiddleware)]) 0))
      (should= 2 (cask/next-state (sut/->CellEngine nil [(->ArithmeticMiddleware)]) 1))
      (should= 4 (cask/next-state (sut/->CellEngine nil [(->MultiplicativeMiddleware)]) 2)))

    (it "reduces multiple middlewares"
      (should= 4 (cask/next-state (sut/->CellEngine nil [(->ArithmeticMiddleware)
                                                         (->MultiplicativeMiddleware)]) 1))
      (should= 3 (cask/next-state (sut/->CellEngine nil [(->MultiplicativeMiddleware)
                                                         (->ArithmeticMiddleware)]) 1))
      (should= 5 (cask/next-state (sut/->CellEngine nil [(->ArithmeticMiddleware)
                                                         (->MultiplicativeMiddleware)
                                                         (->ArithmeticMiddleware)]) 1))
      (should= 8 (cask/next-state (sut/->CellEngine nil [(->ArithmeticMiddleware)
                                                         (->MultiplicativeMiddleware)
                                                         (->MultiplicativeMiddleware)]) 1))))

  (it "renders state with window"
    (cask/render (sut/->CellEngine (h/->window-spec []) []) :state)
    (should-have-invoked :render {:with [:state]})
    (cask/render (sut/->CellEngine (h/->window-spec []) []) :other-state)
    (should-have-invoked :render {:with [:other-state]}))

  )