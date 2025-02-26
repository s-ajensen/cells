(ns cells.middleware.transform-spec
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.middleware.transform :as sut]
            [speclj.core :refer :all]))

(def middleware (sut/->TransformMiddleware))
(def id (ccc/new-uuid))

(defn pos [x y]
  {:position {:x x :y y}})

(describe "Scripting module"

  (context "physics"

    (it "no bodies"
      (let [state {:entities {}}]
        (should= state (cask/next-state middleware state))))

    (it "body with 0 velocity"
      (let [state {:entities
                   {id {:kind      :blah
                        :transform (pos 0 0)
                        :velocity  {:x 0 :y 0}}}}]
        (should= state (cask/next-state middleware state))))

    (it "body with north velocity"
      (let [state {:entities
                   {id {:kind      :blah
                        :transform (pos 0 0)
                        :velocity  {:x 1 :y 0}}}}]
        (should= (pos 1 0)
          (-> (cask/next-state middleware state) :entities (get id) :transform))))

    (it "body with east velocity"
      (let [state {:entities
                   {id {:kind      :blah
                        :transform (pos 0 0)
                        :velocity  {:x 0 :y 1}}}}]
        (should= (pos 0 1)
          (-> (cask/next-state middleware state) :entities (get id) :transform))))

    (it "some bodies without transform"
      (let [id-2 (ccc/new-uuid)
            state {:entities
                   {id   {:kind      :blah
                          :transform (pos 0 0)
                          :velocity  {:x 0 :y 1}}
                    id-2 {:kind :blah}}}]
        (should= (pos 0 1)
          (-> (cask/next-state middleware state) :entities (get id) :transform))
        (should= {:kind :blah} (-> (cask/next-state middleware state) :entities (get id-2)))))))