(ns othello.main
  (:require
    [reagent.core :as r]
    [ysera.error :refer [error]]
    [othello.game.core :as gc]
    [othello.game.api :as ga]
    [othello.view.components :as vc]))

;; State initialization

(defonce app-state-atom (r/atom (gc/create-game)))

;; Event handlers

(defn handle-square-click
  [state {x :x y :y}]
  (println "event: clicked square" x y)
  (ga/play state x y))

(defn handle-restart-click
  [state {}]
  (println "event: clicked restart")
  state)

;; Event dispatcher

(defn trigger-event!
  [{event :event data :data}]
  (condp = event
    :square-click
    (swap! app-state-atom handle-square-click data)

    :restart-click
    (swap! app-state-atom handle-restart-click data)

    (error "Unrecognized event.")))

;; Entry point

(defn mount-root []
  (r/render [vc/app-view {:app-state-atom app-state-atom
                          :trigger-event  trigger-event!}]
            (.getElementById js/document "app")))

(defn init! []
  (mount-root))
