(ns cells.engine-spec
  (:require [cells.engine :as sut]
            [speclj.core :refer :all]))

(describe "Cells engine"

  (context "physics"

    (it "no bodies"
      (let [state {:entities []}]
        (should= state (sut/next-state state))))

    (it "body with 0 velocity"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 0 :y 0}}]}]
        (should= state (sut/next-state state))))

    (it "body with north velocity"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 1 :y 0}}]}]
        (should= {:x 1 :y 0}
          (-> (sut/next-state state) :entities first :transform))))

    (it "body with east velocity"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 0 :y 1}}]}]
        (should= {:x 0 :y 1}
          (-> (sut/next-state state) :entities first :transform))))

    (it "some bodies without transform"
      (let [state {:entities
                   [{:kind      :blah
                     :transform {:x 0 :y 0}
                     :velocity  {:x 0 :y 1}}
                    {:kind :blah}]}]
        (should= {:x 0 :y 1}
          (-> (sut/next-state state) :entities first :transform))
        (should= {:kind :blah} (-> (sut/next-state state) :entities second)))))

  (context "scripts"

    (it "self, next-state"

      (let [state {:entities
                   [{:kind :blah
                     :scripts
                     [{:kind :self
                       :next-state
                       #(assoc % :foo :bar)}]}]}]
        (should= :bar
          (-> (sut/next-state state) :entities first :foo))))))