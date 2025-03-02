(ns cells.middleware.attract
  (:require [c3kit.apron.corec :as ccc]
            [cask.core :as cask]
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

(deftype AttractMiddleware [spec]
  cask/Steppable
  (next-state [_this state]
    (update state :entities (partial attract-entities spec))))