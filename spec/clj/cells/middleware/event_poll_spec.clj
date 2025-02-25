(ns cells.middleware.event-poll-spec
  (:require [cask.core :as cask]
            [cells.middleware.event :as event]
            [cells.middleware.event-poll :as sut]
            [speclj.core :refer :all]))

(deftype NoEvents []
  sut/Pollable
  (poll-events [this] []))

(deftype OneEvent []
  sut/Pollable
  (poll-events [this] [:my-event]))

(deftype OtherEvent []
  sut/Pollable
  (poll-events [this] [:other-event]))

(deftype MultipleEvents []
  sut/Pollable
  (poll-events [this] [:my-event :other-event]))

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
             (cask/next-state (sut/->EventPollMiddleware (->MultipleEvents)) {}))))