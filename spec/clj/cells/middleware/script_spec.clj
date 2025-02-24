(ns cells.middleware.script-spec
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.middleware.script :as sut]
            [speclj.core :refer :all]))

(def middleware (sut/->ScriptMiddleware))
(def id (ccc/new-uuid))

(describe "Scripting module"

  (context "self"

    (it "next-state"
      (let [state {:entities
                   {id {:kind :blah
                        :id   id
                        :scripts
                        [{:scope :self
                          :next-state
                          #(assoc % :foo :bar)}
                         {:kind :self
                          :next-state
                          #(assoc %1 :foo :bar)}]}}}]
        (should= :bar
          (-> (cask/next-state middleware state) :entities (get id) :foo)))))

  #_(focus-context "world"

      (it "next-state"
        (let [state {:entities
                     [{:kind :blah
                       :scripts
                       [{:scope :*
                         :next-state
                         #(assoc % :foo :bar)}]}]}]
          (should= :bar
            (-> (cask/next-state middleware state) :foo))))))