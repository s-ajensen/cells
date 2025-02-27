(ns cells.entity.core-spec
  (:require [c3kit.apron.corec :as ccc]
            [cells.entity.core :as sut]
            [speclj.core :refer :all]))

(def uuid (atom 0))

(describe "Entity"
  (with-stubs)
  (before (reset! uuid 0))

  (redefs-around [ccc/new-uuid (stub :new-uuid {:invoke #(swap! uuid inc)})])

  (context "add-entity"
    (it "adds entity to empty entities"
      (should= {1 {:id 1 :my :entity}} (sut/add-entity {} {:my :entity}))
      (should-have-invoked :new-uuid {:times 1}))

    (it "adds entity to occupied entities"
      (let [state (sut/add-entity {} {:my :entity})]
        (should= (merge state {2 {:id 2 :other :thing}}) (sut/add-entity state {:other :thing})))
      (should-have-invoked :new-uuid {:times 2}))))