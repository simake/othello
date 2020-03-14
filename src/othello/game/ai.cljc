(ns othello.game.ai
  (:require
    [ysera.test :refer [is= is is-not]]
    [othello.game.state :as gs :refer [w b]]
    [othello.game.logic :as gl]))

(defn get-actions
  [state]
  (gl/valid-moves state (gs/get-player-in-turn state)))

(defn perform-action
  [state [i j]]
  (gl/try-move state (gs/get-player-in-turn state) i j))

(defn evaluate-state
  {:test (fn []
           (is= (-> (gs/create-game [[w w b w]
                                     [0 b 0 w]
                                     [0 0 b 0]
                                     [b 0 w w]])
                    (evaluate-state w))
                2))}
  [state player]
  (let [winner (gl/winner state)
        opponent (gl/get-opponent player)]
    (condp = winner
      player 100
      opponent -100
      (- (gl/count-pieces state player)
         (gl/count-pieces state opponent)))))

(defn minimax
  {:test (fn []
           (is= (-> (gs/create-game [[0 0 0 0]
                                     [0 b 0 b]
                                     [b w 0 b]
                                     [0 0 0 w]])
                    (minimax 1))
                {:value 3 :action [0 3]})
           ; prioritizes winning over capturing
           (is= (-> (gs/create-game [[0 0 0 0]
                                     [0 b 0 b]
                                     [0 w 0 b]
                                     [0 0 0 w]])
                    (minimax 1))
                {:value 100 :action [0 1]})
           ; falls for the trap at depth 1 but not 2
           (let [game (-> (gs/create-game [[w b w w 0 0]
                                           [w b 0 0 w b]])
                          (gl/change-player-in-turn))]
             (is= (minimax game 1)
                  {:value 3 :action [0 4]})
             (is= (minimax game 2)
                  {:value -2 :action [1 3]})))}
  [state depth]
  (letfn [(negamax [state depth player]
            (if (or (= depth 0) (not (nil? (gl/winner state))))
              {:value (evaluate-state state player) :action nil}
              (->> (for [action (get-actions state)]
                     {:state (perform-action state action) :action action})
                   (reduce
                     (fn [max child]
                       (let [result (negamax (:state child) (dec depth) (gl/get-opponent player))
                             value (- (:value result))
                             action (:action child)]
                         (if (> value (:value max))
                           {:value value :action action}
                           max)))
                     {:value -9999999 :action nil}))))]
    (negamax state depth (gs/get-player-in-turn state))))

(defn ai-play
  [state]
  (->> (gs/get-ai-difficulty state)
       (minimax state)
       (:action)
       (apply gl/try-move state (gs/get-player-in-turn state))))
