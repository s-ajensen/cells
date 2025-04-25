(ns cells.state.main-menu
  (:require [cells.entity.button :as button]
            [cells.middleware.window :as window]
            [cells.state.orbs :as orbs]))

(def orb-button
  {:label           "orb-button"
   :transform       {:position {:x 0 :y 0} :size {:x 50 :y 50}}
   :color           {:r 0 :g 0 :b 0 :a 255}
   :global-callback (constantly orbs/state)})

(def state
  {:event-queue []
   :entities
   (-> {}
       (window/add-listeners)
       (button/add orb-button))})