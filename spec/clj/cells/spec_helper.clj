(ns cells.spec-helper
  (:require [clojure.test :refer :all]))

(defn position [x y]
  {:position {:x x :y y}})

(defn size [w h]
  {:size {:x w :y h}})

(defn transform
  ([x y w h]
   (merge (position x y)
          (size w h)))
  ([x y]
   (position x y)))