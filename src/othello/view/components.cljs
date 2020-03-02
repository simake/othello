(ns othello.view.components
  (:require [othello.game.core :as gc]))

(defn square-view
  [value]
  [:input {:type    "button"
           :value   value
           :onClick #(js/console.log "trigger-event :on-square-click {params} <-- fake for now")}])

(defn row-view [vals]
  (reduce (fn [div val] (conj div (square-view val))) [:div] vals))

(defn board-view [state]
  (let [width (gc/get-board-width state)
        squares (gc/get-squares state)]
    (reduce
      (fn [div row-vals] (conj div (row-view row-vals)))
      [:div]
      (partition width squares))))

(defn othello-view [state]
  (board-view state))

(defn app-view [state-atom]
  (let [state @state-atom]
    [:div
     [:h2 "Welcome to Reagent"]
     [othello-view state]]))
