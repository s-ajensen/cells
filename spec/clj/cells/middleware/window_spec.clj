(ns cells.middleware.window-spec
  (:require [cask.core :as cask]
            [cells.middleware.window :as sut]
            [cells.spec-helper :as h]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [speclj.core :refer :all]))

(defn ->tested-engine [events]
  (h/->engine [(sut/->WindowMiddleware (h/->window-spec events))
               (->ScriptMiddleware)
               (->EventMiddleware)]))

(defn ->engine []
  (h/->engine [(->ScriptMiddleware)
               (->EventMiddleware)]))

(defn ->tested-setup [state events]
  (cask/setup (->tested-engine events) state))

(defn ->tested-next [state events]
  (cask/next-state (->tested-engine events) state))

(defn ->next [state]
  (cask/next-state (->engine) state))

(def state :undefined)

(describe "window state"
  (with-stubs)

  (with state (->tested-setup {} []))

  (redefs-around [prn (stub :prn)])

  (it "initializes window on setup"
    @state
    (should-have-invoked :window-init))

  (it "exists"
    (let [base-listeners (h/find-entity @state "base-listeners")]
      (should base-listeners)
      (should= :headless-listener (:kind base-listeners))))

  (it "halts state on window close"
    (should= :halt (->tested-next @state [{:type :window-close}])))

  (it "doesn't halt state on not window close"
    (let [event {:type :something-else}]
      (should= (->next @state) (->tested-next @state [event]))))

  (it "prints on left-click"
    (let [event {:type :mouse-pressed :button 1 :position {:x 100 :y 100}}]
      (should= (->next @state) (->tested-next @state [event]))
      (should-have-invoked :prn {:with ["left"]})))

  (it "doesn't print on non left-click"
    (let [event {:type :mouse-pressed :button 3 :position {:x 100 :y 100}}]
      (should= (->next @state) (->tested-next @state [event]))
      (should-not-have-invoked :prn {:with ["left"]})))

  (it "prints on right-click"
    (let [event {:type :mouse-pressed :button 3 :position {:x 100 :y 100}}]
      (should= (->next @state) (->tested-next @state [event]))
      (should-have-invoked :prn {:with ["right"]})))

  (it "doesn't print on non right-click"
    (let [event {:type :mouse-pressed :button 1 :position {:x 100 :y 100}}]
      (should= (->next @state) (->tested-next @state [event]))
      (should-not-have-invoked :prn {:with ["right"]}))))