(ns cells.engine
  (:require [cask.core :as cask]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event-poll :refer [->EventPollMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.entity :as entity]
            [cells.button :as button]
            [cells.state.window :as window]
            [cells.state.orbs :as orbs]))

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
            :listeners [(button/global-listener (constantly orbs/state))]}))})
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