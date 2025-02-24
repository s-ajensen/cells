(ns cells.module.script-spec
  (:require [cask.core :as cask]
            [cells.module.script :as sut]
            [speclj.core :refer :all]))

(def middleware (sut/->ScriptMiddleware))

(describe "Scripting module"

  (context "scripts"

    (it "self, next-state"

      (let [state {:entities
                   [{:kind :blah
                     :scripts
                     [{:kind :self
                       :triggers [:tick]
                       :next-state
                       #(assoc % :foo :bar)}
                      {:kind :self
                       :trigger :on-collide
                       :next-state
                       #(assoc %1 :foo :bar)}]}]}]
        (should= :bar
          (-> (cask/next-state middleware state) :entities first :foo))))))