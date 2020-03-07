(ns othello.view.components
  (:require
    [othello.game.core :as gc :refer [w b]]))

(defn square-view
  [{i             :i
    j             :j
    value         :value
    trigger-event :trigger-event}]
  [:input {:type    "button"
           :value   "  "
           :style   {:backgroundColor (get {w "white" b "black" 0 "green"} value)}
           :onClick #(trigger-event {:event :square-click
                                     :data  {:i i
                                             :j j}})}])

(defn row-view
  [{i             :i
    values        :values
    trigger-event :trigger-event}]
  (->> (map-indexed
         (fn [idx value]
           (square-view {:i             i
                         :j             idx
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
  (->> (map-indexed
         (fn [idx row-values]
           (row-view {:i             idx
                      :values        row-values
                      :trigger-event trigger-event}))
         (gc/get-board state))
       (reduce
         (fn [div square]
           (conj div square))
         [:div.board])))

(defn othello-view
  [{state         :state
    trigger-event :trigger-event}]
  [:div.othello
   (board-view {:state         state
                :trigger-event trigger-event})
   [:input {:type    "button"
            :value   "Restart"
            :style   {:backgroundColor "grey"}
            :onClick #(trigger-event {:event :restart-click})}]])

(defn app-view
  [{app-state-atom :app-state-atom
    trigger-event  :trigger-event}]
  (let [state @app-state-atom]
    [:div
     [:h2 "Othello"]
     [othello-view {:state         state
                    :trigger-event trigger-event}]]))
