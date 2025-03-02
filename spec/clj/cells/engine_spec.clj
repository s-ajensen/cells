(ns cells.engine-spec
  (:require [cask.core :as cask]
            [cells.spec-helper :as h]
            [cells.state.main-menu :as main-menu]
            [cells.engine :as sut]
            [speclj.core :refer :all]))

(describe "Cells engine"

  #_(it "has main-menu state"
    (should= main-menu/state (cask/setup (h/->engine))))

  )