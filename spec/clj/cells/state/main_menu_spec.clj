(ns cells.state.main-menu-spec
  (:require [cells.entity.window :as window]
            [cells.spec-helper :as h]
            [cells.state.orbs :as orbs]
            [speclj.core :refer :all]
            [cells.state.main-menu :as sut]))

(describe "Main Menu State"
  (with-stubs)

  (redefs-around [prn (stub :prn)])

  (it "has window listeners"
    (should= window/listener (dissoc (h/find-entity sut/state "base-listeners") :id)))

  (context "orb button"

    (it "exists"
      (let [button (h/find-entity sut/state "orb-button")]
        (should button)
        (should= :button (:kind button))
        (should (:render? button))
        (should= (h/transform 0 0 50 50)
                 (:transform button))
        (should= {:r 0 :g 0 :b 0 :a 255}
                 (:color button))))

    (it "sets orbs state when clicked"
      (should= orbs/state
               (-> sut/state
                   (h/->next [{:type :mouse-pressed :button 1 :position {:x 25 :y 25}}]))))

    (it "does nothing state when not clicked"
      (let [valid-event {:type :mouse-pressed :button 1 :position {:x 25 :y 25}}]
        (should= sut/state (-> sut/state (h/->next [(assoc valid-event :button 3)])))
        (should= sut/state (-> sut/state (h/->next [(assoc valid-event :type :mouse-dragged)])))
        (should= sut/state (-> sut/state (h/->next [(assoc valid-event :position {:x 51 :y 51})])))
        (should= sut/state (-> sut/state (h/->next [(assoc valid-event :position {:x -1 :y -1})])))
        (should= sut/state (-> sut/state (h/->next [(assoc valid-event :position {:x 25 :y 51})])))
        (should= sut/state (-> sut/state (h/->next [(assoc valid-event :position {:x 25 :y -1})])))))))