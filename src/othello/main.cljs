(ns othello.main
  (:require
    [reagent.core :as r]
    [ysera.error :refer [error]]
    [othello.game.core :as gc]
    [othello.view.components :as vc]
    [othello.view.events :as ve]))

;; State initialization

(defonce app-state-atom (r/atom (gc/create-game)))

;; Event handlers

(defn handle-square-click
  [state {x :x y :y}]
  (println "clicked square" x y)
  state)

(defn handle-restart-click
  [state {}]
  (println "clicked restart")
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
