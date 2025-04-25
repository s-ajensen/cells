(ns cells.middleware.attract
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
            [cells.entity.core :as entity]
            [clojure.math.combinatorics :as combo]))

(defn- sqr [n] (* n n))
(defn- norm [{:keys [x y]}]
  (let [mag (Math/sqrt (+ (sqr x) (sqr y)))]
    {:x (/ x mag) :y (/ y mag)}))
(defn- sub [trans-1 trans-2]
  {:x (- (:x trans-1) (:x trans-2))
   :y (- (:y trans-1) (:y trans-2))})
(defn- add [trans-1 trans-2]
  {:x (+ (:x trans-1) (:x trans-2))
   :y (+ (:y trans-1) (:y trans-2))})
(defn- scale [{:keys [x y]} n]
  {:x (* x n)
   :y (* y n)})

(defn- attract [{:keys [attractions]} attractor attracted]
  (let [attraction (get attractions [(:color attractor) (:color attracted)] (fn [_ _] 0))
        dir (norm (sub (:position (:transform attractor)) (:position (:transform attracted))))]
    (scale dir (attraction attractor attracted))))

(defn- attract-entities [spec entities]
  (let [pairs (combo/combinations entities 2)
        deltas (reduce
                 (fn [deltas [[_ attractor] [_ attracted]]]
                   (-> (update deltas (:id attracted) conj (attract spec attractor attracted))
                       (update (:id attractor) conj (attract spec attracted attractor))))
                 {} pairs)]
    (reduce-kv
      (fn [entities id deltas]
        (let [delta (reduce add deltas)]
          (update-in entities [id :transform :position] add delta)))
      entities deltas)))

(deftype AttractMiddleware [spec entities]
  cask/Steppable
  (setup [_this state] (update state :entities merge entities))
  (next-state [_this state]
    (update state :entities (partial attract-entities spec))))

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

(defn ->attract-middleware []
  (->AttractMiddleware {:attractions
                        {[green green] (fn [_attractor _attracted] 0.5)
                         [blue blue] (fn [_attractor _attracted] -0.5)
                         [red red] (fn [_attractor _attracted] -0.5)
                         [green red] (fn [_attractor _attracted] 0.5)
                         [red green] (fn [_attractor _attracted] -0.5)
                         [red blue] (fn [_attractor _attracted] 0.5)
                         [blue red] (fn [_attractor _attracted] -0.5)
                         [blue green] (fn [_attractor _attracted] 0.5)
                         [green blue] (fn [_attractor _attracted] -0.5)}}
                       entities))