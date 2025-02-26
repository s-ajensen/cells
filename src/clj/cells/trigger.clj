(ns cells.trigger)

(defn global-left-click? [state self event]
  (and (= (:type event) :mouse-pressed)
       (= (:button event) 1)))

(defn global-right-click? [state self event]
  (and (= (:type event) :mouse-pressed)
       (= (:button event) 3)))

(defn global-window-close? [state self event]
  (= (:type event) :window-close))