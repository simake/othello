(ns othello.game.logic
  (:require
    [ysera.test :refer [is= is is-not]]
    [ysera.error :refer [error]]
    [othello.game.core :as gc :refer [w b]]))

(def get-opponent {w b
                   b w})

(defn captures-in-direction
  "Returns the captures in a certain direction from playing a piece at this position.
  The direction is determined by dx and dy (deltas)."
  {:test (fn []
           ; valid flips for white northward
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction w 1 0 0 1))
                [[1 1]])
           ; east
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction w 0 1 1 0))
                [[1 1]])
           ; and southwest
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 0 0 w)
                    (gc/set-square 1 1 b)
                    (gc/set-square 2 2 b)
                    (captures-in-direction w 3 3 -1 -1))
                [[2 2] [1 1]])
           ; invalid for black
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction b 1 0 0 1))
                nil)
           ; valid for black
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction b 3 1 -1 0))
                [[2 1]])
           ; going out of bounds
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction w 2 3 0 1))
                nil))}
  [state player x y dx dy]
  {:pre [(contains? #{1 0 -1} dx) (contains? #{1 0 -1} dy)]}
  (loop [x (+ x dx)
         y (+ y dy)
         captures []]
    (let [square (gc/get-square state x y)
          opponent (get-opponent player)]
      (cond
        (= square opponent) (recur (+ x dx) (+ y dy) (conj captures [x y]))
        (and (= square player) (not-empty captures)) captures
        :else nil))))

(defn valid-move?                                           ;TODO
  [state player x y]
  (when-not (= player (gc/get-player-in-turn state))
    false)
  true)                                                     ;TODO

(defn valid-moves                                           ;TODO
  [state player])
