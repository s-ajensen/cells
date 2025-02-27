(ns cells.state.window-spec
  (:require [cells.state.window :as sut]
            [cells.spec-helper :as h]
            [speclj.core :refer :all]))

(def state {:event-queue []
            :entities (sut/add-listeners {})})

(describe "window state"
  (with-stubs)

  (redefs-around [prn (stub :prn)])

  (it "exists"
    (let [base-listeners (h/find-entity state "base-listeners")]
      (should base-listeners)
      (should= :headless-listener (:kind base-listeners))))

  (it "halts state on window close"
    (should= :halt
             (-> state (h/->next [{:type :window-close}]))))

  (it "doesn't halt state on not window close"
    (should= state
             (-> state (h/->next [{:type :something-else}]))))

  (it "prints on left-click"
    (should= state
             (-> state (h/->next [{:type :mouse-pressed :button 1 :position {:x 100 :y 100}}])))
    (should-have-invoked :prn {:with ["left"]}))

  (it "doesn't print on non left-click"
    (should= state
             (-> state (h/->next [{:type :mouse-pressed :button 3 :position {:x 100 :y 100}}])))
    (should-not-have-invoked :prn {:with ["left"]}))

  (it "prints on right-click"
    (should= state
             (-> state (h/->next [{:type :mouse-pressed :button 3 :position {:x 100 :y 100}}])))
    (should-have-invoked :prn {:with ["right"]}))

  (it "doesn't print on non right-click"
    (should= state
             (-> state (h/->next [{:type :mouse-pressed :button 1 :position {:x 100 :y 100}}])))
    (should-not-have-invoked :prn {:with ["right"]})))