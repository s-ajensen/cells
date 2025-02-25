(ns cells.middleware.event-poll-spec
  (:require [cask.core :as cask]
            [cells.middleware.event :as event]
            [cells.middleware.event-poll :as sut]
            [speclj.core :refer :all]))

(deftype NoEvents []
  sut/Pollable
  (poll-events [this state] []))

(deftype OneEvent []
  sut/Pollable
  (poll-events [this state] [:my-event]))

(deftype OtherEvent []
  sut/Pollable
  (poll-events [this state] [:other-event]))

(deftype MultipleEvents []
  sut/Pollable
  (poll-events [this state] [:my-event :other-event]))

(deftype StateEvents []
  sut/Pollable
  (poll-events [this state] (if (= (:custom-state state) 1) [:my-event] [])))

(describe "Event Polling Middleware"

  (it "enqueues no events"
    (should= {} (cask/next-state (sut/->EventPollMiddleware (->NoEvents)) {})))

  (it "enqueues an event"
    (should= (event/enqueue-event {} :my-event)
             (cask/next-state (sut/->EventPollMiddleware (->OneEvent)) {})))

  (it "enqueues a different event"
    (should= (event/enqueue-event {} :other-event)
             (cask/next-state (sut/->EventPollMiddleware (->OtherEvent)) {})))

  (it "enqueues multiple events"
    (should= (-> (event/enqueue-event {} :my-event) (event/enqueue-event :other-event))
             (cask/next-state (sut/->EventPollMiddleware (->MultipleEvents)) {})))

  (context "polls events based on state"
    (it "default state"
      (should= {} (cask/next-state (sut/->EventPollMiddleware (->StateEvents)) {})))

    (it "custom state"
      (let [custom-state {:custom-state 1}]
        (should= (event/enqueue-event custom-state :my-event) (cask/next-state (sut/->EventPollMiddleware (->StateEvents)) custom-state))))))