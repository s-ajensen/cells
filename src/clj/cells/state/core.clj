(ns cells.state.core
  (:require [cask.core :as cask]))

(defn reduce-middlewares [f state middlewares]
  (reduce (fn [state middleware] (f middleware state)) state middlewares))

(defn reduce-setup [state middlewares]
  (reduce-middlewares cask/setup state middlewares))

(defn reduce-next-state [state middlewares]
  (reduce-middlewares cask/next-state state middlewares))