(ns cells.engine
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.entity.core :as entity]
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

(defn reduce-middlewares [f state middlewares]
  (reduce (fn [state middleware] (f middleware state)) state middlewares))

(deftype CellEngine [window middlewares]
  cask/Steppable
  (setup [_this state]
    (reduce-middlewares cask/setup state middlewares))
  (next-state [_this state]
    (reduce-middlewares cask/next-state state middlewares))
  cask/Renderable
  (render [_this state]
    (cask/render (:renderer window) state)))