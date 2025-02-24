(ns cells.middleware.transform-spec
  (:require [cask.core :as cask]
            [cells.middleware.transform :as sut]
            [speclj.core :refer :all]))

(def middleware (sut/->TransformMiddleware))

(describe "Scripting module"

  (context "physics"

    (it "no bodies"
      (let [state {:entities []}]
        (should= state (cask/next-state middleware state))))

    (it "body with 0 velocity"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 0 :y 0}}]}]
        (should= state (cask/next-state middleware state))))

    (it "body with north velocity"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 1 :y 0}}]}]
        (should= {:x 1 :y 0}
          (-> (cask/next-state middleware state) :entities first :transform))))

    (it "body with east velocity"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 0 :y 1}}]}]
        (should= {:x 0 :y 1}
          (-> (cask/next-state middleware state) :entities first :transform))))

    (it "some bodies without transform"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 0 :y 1}}
                    {:kind :blah}]}]
        (should= {:x 0 :y 1}
          (-> (cask/next-state middleware state) :entities first :transform))
        (should= {:kind :blah} (-> (cask/next-state middleware state) :entities second))))))