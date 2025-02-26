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

(def state (cask/setup (->engine [])))

(defn ->next
  ([state] (->next state []))
  ([state window-events]
   (cask/next-state (->engine window-events) state)))

(defn find-entity [state label]
  (ccc/ffilter #(= label (:label %)) (vals (:entities state))))

(describe "Cells engine"
  (with-stubs)

  (redefs-around [prn (stub :prn)])

  (context "base listeners"
    (it "exists"
      (let [base-listeners (find-entity state "base-listeners")]
        (should base-listeners)
        (should= :headless-listener (:kind base-listeners))))

    (it "halts state on window close"
      (should= :halt
               (-> state (->next [{:type :window-close}]))))

    (it "doesn't halt state on not window close"
      (should= state
               (-> state (->next [{:type :something-else}]))))

    (it "prints on left-click"
      (should= state
               (-> state (->next [{:type :mouse-pressed :button 1 :position {:x 100 :y 100}}])))
      (should-have-invoked :prn {:with ["left"]}))

    (it "doesn't print on non left-click"
      (should= state
               (-> state (->next [{:type :mouse-pressed :button 3 :position {:x 100 :y 100}}])))
      (should-not-have-invoked :prn {:with ["left"]}))

    (it "prints on right-click"
      (should= state
               (-> state (->next [{:type :mouse-pressed :button 3 :position {:x 100 :y 100}}])))
      (should-have-invoked :prn {:with ["right"]}))

    (it "doesn't print on non right-click"
      (should= state
               (-> state (->next [{:type :mouse-pressed :button 1 :position {:x 100 :y 100}}])))
      (should-not-have-invoked :prn {:with ["right"]})))

  (context "orb button"
    (it "exists"
      (let [button (find-entity state "orb-button")]
        (should button)
        (should= :button (:kind button))
        (should (:render? button))
        (should= (spec-helper/transform 0 0 50 50)
                 (:transform button))
        (should= {:r 0 :g 0 :b 0 :a 255}
                 (:color button))))

    (it "sets orbs state when clicked"
      (should= sut/orbs-state
               (-> state
                   (->next [{:type :mouse-pressed :button 1 :position {:x 25 :y 25}}]))))

    (it "does nothing state when not clicked"
      (let [valid-event {:type :mouse-pressed :button 1 :position {:x 25 :y 25}}]
        (should= state (-> state (->next [(assoc valid-event :button 3)])))
        (should= state (-> state (->next [(assoc valid-event :type :mouse-dragged)])))
        (should= state (-> state (->next [(assoc valid-event :position {:x 51 :y 51})])))
        (should= state (-> state (->next [(assoc valid-event :position {:x -1 :y -1})])))
        (should= state (-> state (->next [(assoc valid-event :position {:x 25 :y 51})])))
        (should= state (-> state (->next [(assoc valid-event :position {:x 25 :y -1})]))))))
  )