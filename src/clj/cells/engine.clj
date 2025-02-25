(ns cells.engine
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.window :refer [->WindowMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.window :as window]
            [cells.entity :as entity]))

(def w 800)
(def h 600)

(defn spin [{:keys [tick] :as e}]
  (-> (update e :tick inc)
      (assoc-in [:velocity :x] (* 2 (Math/cos (* 0.1 tick))))
      (assoc-in [:velocity :y] (* 2 (Math/sin (* 0.1 tick))))))

(deftype CellEngine [window]
  cask/Steppable
  (setup [_this]
    {:tick     1
     :entities (-> {}
                   (entity/add-entity
                     {:kind      :cell
                      :transform {:x 0 :y 0}
                      :velocity  {:x 1 :y 1}
                      :tick      0
                      :scripts
                      [{:scope      :self
                        :next-state spin}]})
                   (entity/add-entity
                     {:kind  :spawner
                      :color {:a 0}
                      :scripts
                      [{:scope :*
                        :next-state
                        (fn [state _]
                          (let [id (ccc/new-uuid)
                                entity {:kind      :cell
                                        :transform {:x (- 250 (rand-int 800))
                                                    :y (- 250 (rand-int 800))}
                                        :color {:r (rand-int 255)
                                                :g (rand-int 255)
                                                :b (rand-int 255)
                                                :a (+ 205 (rand-int 50))}
                                        :tick      0
                                        :scripts
                                        [{:scope      :self
                                          :next-state spin}]}]
                            (update state :entities assoc id entity)))}]})
                   (entity/add-entity
                     {:kind      :headless-listener
                      :listeners
                      [{:event      :window-close
                        :scope      :*
                        :next-state (constantly :halt)}
                       {:event      :left-click
                        :scope      :self
                        :next-state #(do (prn "left") %)}
                       {:event      :right-click
                        :scope      :self
                        :next-state #(do (prn "right") %)}]}))})
  (next-state [_this state]
    (reduce (fn [state middleware] (cask/next-state middleware state)) state
            [(->TransformMiddleware)
             (->ScriptMiddleware)
             (->WindowMiddleware window)
             (->EventMiddleware)]))
  cask/Renderable
  (render [_this state]
    (window/render window state)))