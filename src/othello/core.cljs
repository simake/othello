(ns othello.core
  (:require
    [reagent.core :as r]
    [othello.game.core :as gc]
    [othello.view.components :as vc]))

(defonce state-atom (r/atom (gc/create-game)))

; Handle events here?

(defn mount-root []
  (r/render [vc/app-view state-atom] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
