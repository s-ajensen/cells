(ns cells.state.orbs
  (:require [cells.state.entity :as entity]
            [cells.state.window :as window]))

(defn spin [{:keys [tick] :as e}]
  (-> (update e :tick inc)
      (assoc-in [:velocity :x] (* 2 (Math/cos (* 0.1 tick))))
      (assoc-in [:velocity :y] (* 2 (Math/sin (* 0.1 tick))))))

(def state
  {:event-queue []
   :entities
   (-> {}
       (window/add-listeners)
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
                                        {:x (rand-int window/w)
                                         :y (rand-int window/h)}}
                            :color     {:r (rand-int 255)
                                        :g (rand-int 255)
                                        :b (rand-int 255)
                                        :a (+ 205 (rand-int 50))}
                            :tick      0
                            :scripts
                            [{:scope      :self
                              :next-state spin}]}]
                (update state :entities entity/add-entity entity)))}]}))})