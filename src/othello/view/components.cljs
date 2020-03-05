(ns othello.view.components
  (:require
    [othello.game.core :as gc :refer [w b]]))

(defn square-view
  [{x             :x
    y             :y
    value         :value
    trigger-event :trigger-event}]
  [:input {:type    "button"
           :value   "  "
           :style   {:backgroundColor (get {w "white" b "black" 0 "green"} value)}
           :onClick #(trigger-event {:event :square-click
                                     :data  {:x x
                                             :y y}})}])

(defn row-view
  [{y             :y
    values        :values
    trigger-event :trigger-event}]
  (->> (map-indexed
         (fn [idx value]
           (square-view {:x             idx
                         :y             y
                         :value         value
                         :trigger-event trigger-event}))
         values)
       (reduce
         (fn [div square]
           (conj div square))
         [:div.row])))

(defn board-view
  [{state         :state
    trigger-event :trigger-event}]
  (let [width (gc/get-board-width state)
        height (gc/get-board-height state)
        squares (gc/get-squares state)
        rows (partition width squares)]
    (->> (map-indexed
           (fn [idx row-values]
             (row-view {:y             (- height idx 1)
                        :values        row-values
                        :trigger-event trigger-event}))
           rows)
         (reduce
           (fn [div square]
             (conj div square))
           [:div.board]))))

(defn othello-view
  [{state         :state
    trigger-event :trigger-event}]
  (board-view {:state         state
               :trigger-event trigger-event}))

(defn app-view
  [{app-state-atom :app-state-atom
    trigger-event  :trigger-event}]
  (let [state @app-state-atom]
    [:div
     [:h2 "Othello"]
     [othello-view {:state         state
                    :trigger-event trigger-event}]]))
