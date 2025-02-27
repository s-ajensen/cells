(ns cells.engine
  (:require [cask.core :as cask]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event-poll :refer [->EventPollMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.entity :as entity]
            [cells.button :as button]
            [cells.state.window :as window]))

(def w 800)
(def h 600)

(defn spin [{:keys [tick] :as e}]
  (-> (update e :tick inc)
      (assoc-in [:velocity :x] (* 2 (Math/cos (* 0.1 tick))))
      (assoc-in [:velocity :y] (* 2 (Math/sin (* 0.1 tick))))))

(def orbs-state
  {:event-queue []
   :entities
   (-> {}
       (window/add-entities)
       (entity/add-entity
         {:kind      :cell
          :render?   true
          :transform {:position {:x 0 :y 0}}
          :tick      0
          :scripts
          [{:scope      :self
            :next-state spin}]})
       (entity/add-entity
         {:kind :spawner
          :scripts
          [{:scope :*
            :next-state
            (fn [state _]
              (let [entity {:kind      :cell
                            :render?   true
                            :transform {:position
                                        {:x (rand-int w)
                                         :y (rand-int h)}}
                            :color     {:r (rand-int 255)
                                        :g (rand-int 255)
                                        :b (rand-int 255)
                                        :a (+ 205 (rand-int 50))}
                            :tick      0
                            :scripts
                            [{:scope      :self
                              :next-state spin}]}]
                (update state :entities entity/add-entity entity)))}]}))})

(deftype CellEngine [window]
  cask/Steppable
  (setup [_this]
    {:event-queue []
     :entities
     (-> {}
         (window/add-entities)
         (entity/add-entity
           {:label     "orb-button"
            :kind      :button
            :render?   true
            :transform {:position {:x 0 :y 0} :size {:x 50 :y 50}}
            :color     {:r 0 :g 0 :b 0 :a 255}
            :listeners [(button/global-listener (constantly orbs-state))]}))})
  (next-state [_this state]
    ; TODO - use cask/Steppable's `setup` fn with the middleware.
    ;; (CellEngine's setup should just be a `reduce` of the middleware setups)
    (reduce (fn [state middleware] (cask/next-state middleware state)) state
            [(->TransformMiddleware)
             (->ScriptMiddleware)
             (->EventPollMiddleware (:event-poller window))
             (->EventMiddleware)]))
  cask/Renderable
  (render [_this state]
    (cask/render (:renderer window) state)))