(ns cells.engine
  (:require [cask.core :as cask]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event-poll :refer [->EventPollMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.entity :as entity]))

(def w 800)
(def h 600)

(defn spin [{:keys [tick] :as e}]
  (-> (update e :tick inc)
      (assoc-in [:velocity :x] (* 2 (Math/cos (* 0.1 tick))))
      (assoc-in [:velocity :y] (* 2 (Math/sin (* 0.1 tick))))))

(defn trigger-left-click? [state self event]
  (and (= (:type event) :mouse-pressed)
       (= (:button event) 1)))

(defn trigger-right-click? [state self event]
  (and (= (:type event) :mouse-pressed)
       (= (:button event) 3)))

(defn trigger-window-close? [state self event]
  (= (:type event) :window-close))

(def base-listeners
  {:kind :headless-listener
   :listeners
   [{:scope      :*
     :trigger    trigger-window-close?
     :next-state (constantly :halt)}
    {:scope      :*
     :trigger    trigger-left-click?
     :next-state (fn [state self event] (do (prn "left") state))}
    {:scope      :*
     :trigger    trigger-right-click?
     :next-state (fn [state self event] (do (prn "right") state))}]})

(def orbs-state
  {:event-queue []
   :entities
   (-> {}
       (entity/add-entity base-listeners)
       (entity/add-entity
         {:kind      :cell
          :render?   true
          :transform {:x 0 :y 0}
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
                            :transform {:x (- 400 (rand-int 800))
                                        :y (- 400 (rand-int 800))}
                            :color     {:r (rand-int 255)
                                        :g (rand-int 255)
                                        :b (rand-int 255)
                                        :a (+ 205 (rand-int 50))}
                            :tick      0
                            :scripts
                            [{:scope      :self
                              :next-state spin}]}]
                (update state :entities entity/add-entity entity)))}]}))})

(defn point-in-transform? [{:keys [x y] :as point} transform]
  (let [tx (:x (:position transform))
        ty (:y (:position transform))
        tw (:x (:size transform))
        th (:y (:size transform))]
    (and (<= x (+ tx tw))
         (>= x tx)
         (<= y (+ ty th))
         (>= y ty))))

(defn call-if-event-in-transform [f state self event]
  (if (point-in-transform? (:position event) (:transform self))
    (f state self event)
    state))

(defn button-clicked-listener [callback]
  {:scope      :*
   :trigger    trigger-left-click?
   :next-state (partial call-if-event-in-transform callback)})

(deftype CellEngine [window]
  cask/Steppable
  (setup [_this]
    {:event-queue []
     :entities
     (-> {}
         (entity/add-entity base-listeners)
         (entity/add-entity
           {:label      "orb-button"
            :kind      :button
            :render?   true
            :transform {:position {:x 0 :y 0} :size {:x 50 :y 50}}
            :listeners [(button-clicked-listener (constantly orbs-state))]}))})
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