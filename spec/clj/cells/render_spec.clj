(ns cells.render-spec
  (:require [c3kit.apron.corec :as ccc]
            [cells.render :as sut]
            [clojure2d.core :as c2d]
            [speclj.core :refer :all]))

(def state
  (let [id-1 (ccc/new-uuid)
        id-2 (ccc/new-uuid)
        id-3 (ccc/new-uuid)
        id-4 (ccc/new-uuid)]
    {:kind     :state
     :entities {id-1 {:kind :cell}
                id-2 {:kind      :cell
                      :transform {:x 10 :y 10}}
                id-3 {:kind  :cell
                      :color {:r 255 :g 255 :b 255 :a 255}}
                id-4 {:kind   :cell
                      :radius 10}}}))

(describe "Cells simulation renderer"

  (with-stubs)

  (redefs-around [c2d/set-color (stub :set-color)
                  c2d/ellipse (stub :ellipse)])

  (context "cell"

    (before (sut/render :canvas state))

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
      (should-have-invoked :ellipse {:with [:canvas 0 0 10 10]}))))