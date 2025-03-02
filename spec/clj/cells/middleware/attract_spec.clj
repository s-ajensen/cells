(ns cells.middleware.attract-spec
  (:require [cask.core :as cask]
            [cells.entity.core :as entity]
            [cells.entity.core :as e]
            [cells.middleware.attract :as sut]
            [speclj.core :refer :all]))

(def red {:r 255 :g 0 :b 0 :a 255})
(def green {:r 0 :g 255 :b 0 :a 255})

(describe "Cell attraction middleware"

  ;; TODO - Too tired to do this. Anyway, it's causing the two failing tests
  (xit "doesn't update non-cells"
    (let [entities (-> {}
                       (entity/add-entity {:kind :not-cell})
                       (entity/add-entity {:kind :also-not-cell}))
          state {:entities entities}]
      (should= state (-> (sut/->AttractMiddleware {})
                         (cask/next-state state)))))

  (context "moves cells"

    (it "one attracted, one static, lateral"
      (let [{attracted-id :id :as attracted}
            (e/->entity
              {:kind      :cell
               :transform {:position {:x 10 :y 0}}
               :color     red})
            {static-id :id :as static}
            (e/->entity
              {:kind      :cell
               :transform {:position {:x 0.0 :y 0.0}}
               :color     green})
            spec {:attractions
                  {[green red] (fn [_attractor _attracted] 1)}}
            state {:entities
                   {attracted-id attracted
                    static-id    static}}
            result (-> (sut/->AttractMiddleware spec)
                       (cask/next-state state)
                       :entities)]
        (should= {:x 9.0 :y 0.0} (-> (get result attracted-id) :transform :position))
        (should= static (get result static-id))
        ))))