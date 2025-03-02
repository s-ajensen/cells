(ns cells.engine
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.entity.core :as entity]
            [cells.middleware.transform :refer [->TransformMiddleware]]
            [cells.middleware.attract :refer [->AttractMiddleware]]
            [cells.middleware.script :refer [->ScriptMiddleware]]
            [cells.middleware.event-poll :refer [->EventPollMiddleware]]
            [cells.middleware.event :refer [->EventMiddleware]]
            [cells.state.main-menu :as main-menu]))

(def red {:r 255 :g 0 :b 0 :a 255})
(def green {:r 0 :g 255 :b 0 :a 255})
(def blue {:r 0 :g 0 :b 255 :a 255})

(defn ->entities [n]
  (reduce (fn [entities _]
            (let [color (rand-nth [red green blue])
                  position {:x (+ 100 (rand-int 400)) :y (+ 100 (rand-int 400))}]
              (entity/add-entity entities {:kind      :cell
                                           :render?   true
                                           :transform {:position position}
                                           :color     color})))
          {}
          (range n)))

(def entities
  (-> (entity/add-entity {} {:kind      :cell
                             :render?   true
                             :transform {:position {:x 300 :y 200}}
                             :color     red})
      (entity/add-entity {:kind      :cell
                          :render?   true
                          :transform {:position {:x 200 :y 200}}
                          :color     green})
      (entity/add-entity {:kind      :cell
                          :render?   true
                          :transform {:position {:x 250 :y 312}}
                          :color     blue})
      (merge (->entities 30))))

(deftype CellEngine [window]
  cask/Steppable
  (setup [_this] {:entities entities})
  (next-state [_this state]
    ; TODO - use cask/Steppable's `setup` fn with the middleware.
    ;; (CellEngine's setup should just be a `reduce` of the middleware setups)
    (reduce (fn [state middleware] (cask/next-state middleware state)) state
            [(->AttractMiddleware {:attractions
                                   {[green green] (fn [_attractor _attracted] 0.5)
                                    [blue blue] (fn [_attractor _attracted] -0.5)
                                    [red red] (fn [_attractor _attracted] -0.5)
                                    [green red] (fn [_attractor _attracted] 0.5)
                                    [red green] (fn [_attractor _attracted] -0.5)
                                    [red blue] (fn [_attractor _attracted] 0.5)
                                    [blue red] (fn [_attractor _attracted] -0.5)
                                    [blue green] (fn [_attractor _attracted] 0.5)
                                    [green blue] (fn [_attractor _attracted] -0.5)}})
             (->TransformMiddleware)
             (->ScriptMiddleware)
             (->EventPollMiddleware (:event-poller window))
             (->EventMiddleware)]))
  cask/Renderable
  (render [_this state]
    (cask/render (:renderer window) state)))