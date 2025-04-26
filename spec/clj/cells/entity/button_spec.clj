(ns cells.entity.button-spec
  (:require [c3kit.apron.corec :as ccc]
            [cells.entity.button :as sut]
            [cells.spec-helper :as h]
            [cells.middleware.window :refer [->WindowMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [speclj.core :refer :all]))

(defn first-entity [entities]
  (second (first entities)))

(def state :undefined)

(defn ->next [state events]
  (h/->next state [(->WindowMiddleware (h/->window-spec events))
                   (->EventMiddleware)]))

(describe "Button Entity"
  (with-stubs)

  (redefs-around [ccc/new-uuid (stub :new-uuid {:return "123"})])

  (after (should-have-invoked :new-uuid))

  (it "adds label"
    (should= "my-label"
             (:label (first-entity (sut/add {} {:label "my-label"})))))

  (it "adds transform"
    (should= {:position {:x 5 :y 2}}
             (:transform (first-entity (sut/add {} {:transform {:position {:x 5 :y 2}}})))))

  (it "adds color"
    (should= {:r 1 :g 6 :b 9 :a 45}
             (:color (first-entity (sut/add {} {:color {:r 1 :g 6 :b 9 :a 45}})))))

  (context "listeners"

    (with state {:entities
                 (sut/add {} {:transform {:position {:x 2 :y 10} :size {:x 20 :y 15}}
                              :global-callback (constantly {:my :state})})})

    (it "updates state when triggering listener callback"
      (let [event {:type :mouse-pressed :button 1 :position {:x 2 :y 10}}]
        (should= {:my :state} (->next @state [event]))))

    (context "doesn't update state when listener not triggered"

      (it "to the left of button"
        (let [event {:type :mouse-pressed :button 1 :position {:x 1 :y 10}}]
          (should= @state (->next @state [event]))))

      (it "to the right of button"
        (let [event {:type :mouse-pressed :button 1 :position {:x 23 :y 10}}]
          (should= @state (->next @state [event]))))

      (it "above button"
        (let [event {:type :mouse-pressed :button 1 :position {:x 2 :y 9}}]
          (should= @state (->next @state [event]))))

      (it "below button"
        (let [event {:type :mouse-pressed :button 1 :position {:x 2 :y 26}}]
          (should= @state (->next @state [event])))))))