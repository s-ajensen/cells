(ns cells.render
  (:require [cask.core :as cask]
            [cells.engine :as engine]
            [clojure2d.core :as c2d]
            [cells.middleware.event-poll :as poll]))

(def origin {:x 0 :y 0})
(def default-color {:r 254 :g 0 :b 220 :a 255})
(def default-radius 25)

(defn render-cell [canvas {:keys [transform color radius] :as cell}]
  (let [{:keys [x y]} (merge origin transform)
        {:keys [r g b a]} (merge default-color color)
        radius (or radius default-radius)]
    (c2d/set-color canvas r g b a)
    (c2d/ellipse canvas x y radius radius)))

;; TODO - be able to render buttons
(defn render [canvas {:keys [entities]}]
  (run! (partial render-cell canvas) (filter :render? (vals entities))))

(defn render-state [canvas state]
  (c2d/with-canvas-> canvas
                     (c2d/set-background :white)
                     (c2d/translate 400 300)
                     (render state)))

(deftype C2DRenderer [window]
  cask/Renderable
  (render [_this state]
    (let [canvas (c2d/canvas (:w window) (:h window))]
      (c2d/with-canvas-> canvas
                         (render-state state))
      (c2d/replace-canvas window canvas)
      (c2d/repaint window))))

;; [GMJ] we use atoms here since c2d only supports simultaneous events via multimethods
;; whose state can't be known outside of the scope of the window without atoms (I think)
(def events (atom []))

(deftype C2DPoller [window]
  poll/Pollable
  (poll-events [this state]
    (if-not (c2d/window-active? window)
      (swap! events conj {:type :window-close}))
    (let [polled-events @events]
      (reset! events [])
      polled-events)))

(defn init! [window]
  (defmethod c2d/mouse-event [(:window-name window) :mouse-pressed] [event state]
    (let [position (c2d/mouse-pos window)]
      (swap! events conj {:type :mouse-pressed
                          :button (.getButton event)
                          :position {:x (.-x position) :y (.-y position)}}))
    state))