(ns othello.view.components
  (:require
    [othello.game.core :as gc]))

(defn square-view
  [{x             :x
    y             :y
    value         :value
    trigger-event :trigger-event}]
  [:input {:type    "button"
           :value   value
           :onClick #(trigger-event {:event :square-click
                                     :data  {:x x
                                             :y y}})}])

(defn row-view
  [{values        :values
    trigger-event :trigger-event}]
  (reduce
    (fn [div value]
      (conj div (square-view {:x             "?"            ;TODO
                              :y             "?"
                              :value         value
                              :trigger-event trigger-event})))
    [:div.row]
    values))

(defn board-view
  [{state         :state
    trigger-event :trigger-event}]
  (let [width (gc/get-board-width state)
        squares (gc/get-squares state)]
    (reduce
      (fn [div row-values]
        (conj div (row-view {:values        row-values
                             :trigger-event trigger-event})))
      [:div.board]
      (partition width squares))))

(defn othello-view
  [{state         :state
    trigger-event :trigger-event}]
  (board-view {:state         state
               :trigger-event trigger-event}))

(defn app-view
  [{app-state-atom :app-state-atom
    trigger-event  :trigger-event}]
  (let [state @app-state-atom]
    [:h2 "Welcome to Reagent"]
    [othello-view {:state         state
                   :trigger-event trigger-event}]))
