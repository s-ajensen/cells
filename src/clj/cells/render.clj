(ns cells.render
  (:require [clojure2d.core :as c2d]))

(def origin {:x 0 :y 0})
(def default-color {:r 254 :g 0 :b 220 :a 255})
(def default-radius 25)

(defn render-cell [canvas {:keys [transform color radius] :as cell}]
  (let [{:keys [x y]} (merge origin transform)
        {:keys [r g b a]} (merge default-color color)
        radius (or radius default-radius)]
    (c2d/set-color canvas r g b a)
    (c2d/ellipse canvas x y radius radius)))

(defn render [canvas {:keys [entities]}]
  (run! (partial render-cell canvas) entities))

(defn render-state [canvas state]
  (c2d/with-canvas-> canvas
                     (c2d/set-background :white)
                     (c2d/translate 400 300)
                     (render state)))