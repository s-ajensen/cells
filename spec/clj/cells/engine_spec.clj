(ns cells.engine-spec
  (:require [cells.spec-helper :as h]
            [speclj.core :refer :all]
            [cells.engine :as sut]))

(describe "Cells engine"

  (context "orb button"

    (it "exists"
      (let [button (h/find-entity h/state "orb-button")]
        (should button)
        (should= :button (:kind button))
        (should (:render? button))
        (should= (h/transform 0 0 50 50)
                 (:transform button))
        (should= {:r 0 :g 0 :b 0 :a 255}
                 (:color button))))

    (it "sets orbs state when clicked"
      (should= sut/orbs-state
               (-> h/state
                   (h/->next [{:type :mouse-pressed :button 1 :position {:x 25 :y 25}}]))))

    (it "does nothing state when not clicked"
      (let [valid-event {:type :mouse-pressed :button 1 :position {:x 25 :y 25}}]
        (should= h/state (-> h/state (h/->next [(assoc valid-event :button 3)])))
        (should= h/state (-> h/state (h/->next [(assoc valid-event :type :mouse-dragged)])))
        (should= h/state (-> h/state (h/->next [(assoc valid-event :position {:x 51 :y 51})])))
        (should= h/state (-> h/state (h/->next [(assoc valid-event :position {:x -1 :y -1})])))
        (should= h/state (-> h/state (h/->next [(assoc valid-event :position {:x 25 :y 51})])))
        (should= h/state (-> h/state (h/->next [(assoc valid-event :position {:x 25 :y -1})]))))))
  )