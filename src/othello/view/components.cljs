(ns othello.view.components
  (:require
    [othello.game.state :as gs :refer [w b]]
    [othello.game.logic :as gl]))

(defn piece-view
  [player]
  [:div.piece {:style {:backgroundColor (if (= player w) "white" "#111")
                       :height          "80%"
                       :width           "80%"
                       :borderRadius    "100%"
                       :border          "2px solid black"
                       :box-shadow      "1.5px 1.5px"
                       :background      (str "linear-gradient(135deg, "
                                             (if (= player w)
                                               "white, ghostwhite"
                                               "rgb(30,30,0,1), rgb(0,0,0,0.1)")
                                             ")")
                       :position        "relative"
                       :top             "3%"}}])

(defn square-view
  [{i             :i
    j             :j
    value         :value
    trigger-event :trigger-event}]
  [:button.square {:value   "  "
                   :style   {:display         "flex"
                             :backgroundColor "green"
                             :height          "8vmin"
                             :width           "8vmin"
                             :border          "1px solid black"
                             :justify-content "center"
                             :padding         0}
                   :onClick #(trigger-event {:event :square-click
                                             :data  {:i i
                                                     :j j}})}
   (if (not= value 0)
     (piece-view value)
     nil)])

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
         [:div.row {:style {:display "flex"}}])))

(defn board-view
  [{state         :state
    trigger-event :trigger-event}]
  (->> (map-indexed
         (fn [idx row-values]
           (row-view {:i             idx
                      :values        row-values
                      :trigger-event trigger-event}))
         (gs/get-board state))
       (reduce
         (fn [div square]
           (conj div square))
         [:div.board {:style {:display        "flex"
                              :flex-direction "column"
                              :align-items    "center"
                              :border         "2px solid black"
                              :box-shadow     "1px 2px"}}])))

(defn othello-view
  [{state         :state
    trigger-event :trigger-event}]
  [:div.othello {:style {:display        "flex"
                         :flex-direction "column"
                         :align-items    "center"}}
   [:h3 (condp = (gl/winner state)
          w "Winner: White"
          b "Winner: Black"
          false "Draw"
          nil)]
   (board-view {:state         state
                :trigger-event trigger-event})
   [:div.buttons {:style {:display         "flex"
                          :width           "100%"
                          :margin-top      "30px"
                          :justify-content "center"}}
    [:input {:type    "button"
             :value   "Restart"
             :style   {:backgroundColor "white"
                       :border          "2px solid black"
                       :font-size       "1.5em"}
             :onClick #(trigger-event {:event :restart-click})}]]])

(defn app-view
  [{app-state-atom :app-state-atom
    trigger-event  :trigger-event}]
  (let [state @app-state-atom]
    [othello-view {:state         state
                   :trigger-event trigger-event}]))
