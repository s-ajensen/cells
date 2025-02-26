(ns cells.engine-spec
  (:require [c3kit.apron.corec :as ccc]
            [cells.middleware.event-poll :as poll]
            [cells.spec-helper :as spec-helper]
            [speclj.core :refer :all]
            [cask.core :as cask]
            [cells.engine :as sut])
  (:import (cells.engine CellEngine)))

(deftype WindowRenderer []
  cask/Renderable
  (render [_this _state]
    ((stub :render))))

(deftype WindowPoller [events]
  poll/Pollable
  (poll-events [_this _state]
    events))

(defn ->window [window-events]
  {:renderer (->WindowRenderer)
   :event-poller (->WindowPoller window-events)})

(defn ->engine [window-events]
  (CellEngine. (->window window-events)))

(defn ->setup []
  (cask/setup (->engine [])))

(defn ->next
  ([state] (->next state []))
  ([state window-events]
   (cask/next-state (->engine window-events) state)))

(defn find-entity [state label]
  (ccc/ffilter #(= label (:label %)) (vals (:entities state))))

(describe "Cells engine"

  (context "orb button"
    (it "exists"
      (let [button (find-entity (->setup) "orb-button")]
        (should button)
        (should= :button (:kind button))
        (should (:render? button))
        (should= (spec-helper/transform 0 0 50 50)
                 (:transform button))
        (should= {:r 0 :g 0 :b 0 :a 255}
                 (:color button))))

    (it "sets orbs state when clicked"
      (should= sut/orbs-state
               (-> (->setup)
                   (->next [{:type :mouse-pressed :button 1 :position {:x 25 :y 25}}]))))

    (it "does nothing state when not clicked"
      (let [valid-event {:type :mouse-pressed :button 1 :position {:x 25 :y 25}}
            state (->setup)]
        (should= state (-> state (->next [(assoc valid-event :button 3)])))
        (should= state (-> state (->next [(assoc valid-event :type :mouse-dragged)])))
        (should= state (-> state (->next [(assoc valid-event :position {:x 51 :y 51})])))
        (should= state (-> state (->next [(assoc valid-event :position {:x -1 :y -1})])))
        (should= state (-> state (->next [(assoc valid-event :position {:x 25 :y 51})])))
        (should= state (-> state (->next [(assoc valid-event :position {:x 25 :y -1})]))))))
  )