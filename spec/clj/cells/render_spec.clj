(ns cells.render-spec
  (:require [cells.entity :as entity]
            [cells.render :as sut]
            [clojure2d.core :as c2d]
            [speclj.core :refer :all]))

(def cell-state
  {:kind     :state
   :entities (-> {}
                 (entity/add-entity {:kind    :cell
                                     :render? true})
                 (entity/add-entity {:kind      :cell
                                     :render?   true
                                     :transform {:position {:x 10 :y 10}}})
                 (entity/add-entity {:kind    :cell
                                     :render? true
                                     :color   {:r 255 :g 255 :b 255 :a 255}})
                 (entity/add-entity {:kind    :cell
                                     :render? true
                                     :radius  10}))})

(def button-state
  {:kind     :state
   :entities (-> {}
                 (entity/add-entity {:kind    :button
                                     :render? true})
                 (entity/add-entity {:kind      :button
                                     :render?   true
                                     :transform {:position {:x 10 :y 10}}})
                 (entity/add-entity {:kind      :button
                                     :render?   true
                                     :transform {:size {:x 50 :y 50}}})
                 (entity/add-entity {:kind    :cell
                                     :render? true
                                     :color   {:r 255 :g 255 :b 255 :a 255}}))})

(def unrenderable-state
  {:kind     :state
   :entities (-> {}
                 (entity/add-entity {:kind    :headless-entity
                                     :render? false}))})

(describe "Cells simulation renderer"

  (with-stubs)

  (redefs-around [c2d/set-color (stub :set-color)
                  c2d/ellipse (stub :ellipse)
                  c2d/rect (stub :rect)])

  (context "renderable"

    (context "cell"
      (before (sut/render :canvas cell-state))

      (it "default"
        (should-have-invoked :set-color {:with [:canvas 254 0 220 255]})
        (should-have-invoked :ellipse {:with [:canvas 0 0 25 25]}))

      (it "with transform"
        (should-have-invoked :set-color {:with [:canvas 254 0 220 255]})
        (should-have-invoked :ellipse {:with [:canvas 10 10 25 25]}))

      (it "with color"
        (should-have-invoked :set-color {:with [:canvas 255 255 255 255]})
        (should-have-invoked :ellipse {:with [:canvas 0 0 25 25]}))

      (it "with radius"
        (should-have-invoked :set-color {:with [:canvas 254 0 220 255]})
        (should-have-invoked :ellipse {:with [:canvas 0 0 10 10]})))

    (context "button"
      (before (sut/render :canvas button-state))

      (it "default"
        (should-have-invoked :set-color {:with [:canvas 254 0 220 255]})
        (should-have-invoked :rect {:with [:canvas 0 0 0 0]}))

      (it "with position"
        (should-have-invoked :set-color {:with [:canvas 254 0 220 255]})
        (should-have-invoked :rect {:with [:canvas 10 10 0 0]}))

      (it "with size"
        (should-have-invoked :set-color {:with [:canvas 254 0 220 255]})
        (should-have-invoked :rect {:with [:canvas 0 0 50 50]}))

      (it "with color"
        (should-have-invoked :set-color {:with [:canvas 255 255 255 255]})
        (should-have-invoked :rect {:with [:canvas 0 0 0 0]}))))

  (it "only renders renderable entities"
    (sut/render :canvas unrenderable-state)
    (should-not-have-invoked :set-color)
    (should-not-have-invoked :ellipse)))