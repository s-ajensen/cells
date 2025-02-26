(ns cells.middleware.event-spec
  (:require [cask.core :as cask]
            [cells.entity :as entity]
            [cells.middleware.event :as sut]
            [speclj.core :refer :all]))

(defn global-inc [state _self]
  (update state :counter inc))

(defn global-double [state _self]
  (update state :counter #(* 2 %)))

(def inc-listener {:trigger #(= % :my-event)
                   :scope :*
                   :next-state global-inc})

(def double-listener {:trigger #(= % :other-event)
                      :scope :*
                      :next-state global-double})

(def halt-listener {:trigger #(= % :halt-event)
                    :scope :*
                    :next-state (constantly :halt)})

(def state {:counter 0 :event-queue [] :entities (-> {} (entity/add-entity {:listeners [inc-listener]}))})

(defn ->state [entities events]
  (assoc state :event-queue events
               :entities (reduce entity/add-entity {} entities)))

(defn with-events [state events]
  (assoc state :event-queue events))

(describe "Event Middleware"
  (with-stubs)

  (context "enqueue-event"
    (it "nil event-queue"
      (should= {:event-queue [:event]} (sut/enqueue-event nil :event)))

    (it "multiple event-queue"
      (should= {:event-queue [:event :other-event]} (-> (sut/enqueue-event nil :event) (sut/enqueue-event :other-event)))))

  (it "invokes listener when event is triggered"
    (should= (inc (:counter state))
             (:counter (cask/next-state (sut/->EventMiddleware) (with-events state [:my-event])))))

  (context "multiple invocations"

    (it "1 entity 2 identical events"
      (let [state (->state [{:listeners [inc-listener]}] [:my-event :my-event])]
        (should= (+ 2 (:counter state))
                 (:counter (cask/next-state (sut/->EventMiddleware) state)))))

    (it "2 identical entities 1 event"
      (let [state (->state [{:listeners [inc-listener]} {:listeners [inc-listener]}] [:my-event])]
        (should= (+ 2 (:counter state))
                 (:counter (cask/next-state (sut/->EventMiddleware) state)))))

    (it "2 different entities 2 different events"
      (let [state (->state [{:listeners [inc-listener]} {:listeners [double-listener]}] [:other-event :my-event])]
        (should= (inc (* 2 (:counter state)))
                 (:counter (cask/next-state (sut/->EventMiddleware) state)))))

    (it "triggers events in order"
      (let [state (->state [{:listeners [inc-listener double-listener]}] [:my-event :other-event])]
        (should= (* 2 (inc (:counter state)))
                 (:counter (cask/next-state (sut/->EventMiddleware) state))))
      (let [state (->state [{:listeners [double-listener inc-listener]}] [:other-event :my-event])]
        (should= (inc (* 2 (:counter state)))
                 (:counter (cask/next-state (sut/->EventMiddleware) state))))))

  (it "doesn't invoke listener when event is not triggered"
    (should= (:counter state)
             (:counter (cask/next-state (sut/->EventMiddleware) state))))

  (it "doesn't invoke listener when no events"
    (should= (:counter state)
             (:counter (cask/next-state (sut/->EventMiddleware) (with-events state [:unknown-event])))))

  (it "removes events after triggering"
    (should= [] (:event-queue (cask/next-state (sut/->EventMiddleware) (with-events state [:my-event]))))
    (should= [] (:event-queue (cask/next-state (sut/->EventMiddleware) (with-events state [:my-event :other-event])))))

  (it "halt persists through event triggers"
    (let [state (->state [{:listeners [inc-listener halt-listener]}] [:halt-event :my-event])]
      (should= :halt (cask/next-state (sut/->EventMiddleware) state))))

  (it "halt persists through halt"
    (should= :halt (cask/next-state (sut/->EventMiddleware) :halt))))