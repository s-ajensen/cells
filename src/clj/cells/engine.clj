(ns cells.engine
  (:require [cask.core :as cask]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.event-poll :refer [->EventPollMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
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
                      :render?   true
                      :transform {:x 0 :y 0}
                      :tick      0
                      :scripts
                      [{:scope      :self
                        :next-state spin}]})
                   (entity/add-entity
                     {:kind    :spawner
                      :scripts
                      [{:scope :*
                        :next-state
                        (fn [state _]
                          state
                          (let [entity {:kind      :cell
                                        :render?   true
                                        :transform {:x (- 400 (rand-int 800))
                                                    :y (- 400 (rand-int 800))}
                                        :color {:r (rand-int 255)
                                                :g (rand-int 255)
                                                :b (rand-int 255)
                                                :a (+ 205 (rand-int 50))}
                                        :tick      0
                                        :scripts
                                        [{:scope      :self
                                          :next-state spin}]}]
                            (update state :entities entity/add-entity entity)))}]})
                   (entity/add-entity
                     {:kind      :headless-listener
                      :listeners
                      [{:event      :window-close
                        :scope      :*
                        :next-state (constantly :halt)}
                       {:event      {:type :mouse-pressed :button 1}
                        :scope      :self
                        :next-state #(do (prn "left") %)}
                       {:event      {:type :mouse-pressed :button 3}
                        :scope      :self
                        :next-state #(do (prn "right") %)}]}))})
  (next-state [_this state]
    (reduce (fn [state middleware] (cask/next-state middleware state)) state
            [(->TransformMiddleware)
             (->ScriptMiddleware)
             (->EventPollMiddleware (:event-poller window))
             (->EventMiddleware)]))
  cask/Renderable
  (render [_this state]
    (cask/render (:renderer window) state)))