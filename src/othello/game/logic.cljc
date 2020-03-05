(ns othello.game.logic
  (:require
    [ysera.test :refer [is= is is-not]]
    [ysera.error :refer [error]]
    [othello.game.core :as gc :refer [w b]]))

(def get-opponent {w b
                   b w})

(defn change-player-in-turn
  [state]
  (gc/set-player-in-turn state (get-opponent (gc/get-player-in-turn state))))

(defn captures-in-direction
  "Returns the captures in a certain direction from playing a piece at this position.
  The direction is determined by dx and dy (deltas)."
  {:test (fn []
           ; white captures northward
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
           ; white would capture but not black
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction b 1 0 0 1))
                [])
           ; black captures westward
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction b 3 1 -1 0))
                [[2 1]])
           ; immediately going out of bounds
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction w 2 3 0 1))
                []))}
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
        :else []))))

(defn captures
  "Returns the captures from playing a piece at this position."
  {:test (fn []
           ; south-west and south (white)
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 0 0 w)
                    (gc/set-square 3 2 b)
                    (gc/set-square 3 1 w)
                    (captures w 3 3)
                    (sort))
                [[1 1] [2 2] [3 2]])
           ; west, north-west, and north (black)
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 0 0 b)
                    (gc/set-square 0 3 b)
                    (gc/set-square 3 3 b)
                    (gc/set-square 2 0 w)
                    (gc/set-square 1 0 w)
                    (gc/set-square 3 2 w)
                    (gc/set-square 3 1 w)
                    (captures b 3 0)
                    (sort))
                [[1 0] [1 2] [2 0] [2 1] [3 1] [3 2]])
           ; no captures
           (is= (-> (gc/create-game 4 4)
                    (captures w 0 0))
                []))}
  [state player x y]
  (let [directions
        (for [dx [1 0 -1]
              dy [1 0 -1]
              :when (not= dx dy 0)]
          [dx dy])]
    (reduce
      (fn [acc direction]
        (into [] (concat acc (apply captures-in-direction state player x y direction))))
      [] directions)))

(defn occupied
  "Returns the player at the coordinates, nil if it isn't occupied."
  [state x y]
  (let [player (gc/get-square state x y)]
    (if (or (= player w) (= player b))
      player
      nil)))

(defn try-move
  "Attempts a move. Returns new state upon success, nil otherwise."
  {:test (fn []
           ; places new piece
           (is= (-> (gc/create-game 4 4)
                    (try-move w 1 0)
                    (gc/get-square 1 0))
                w)
           ; captures piece
           (is= (-> (gc/create-game 4 4)
                    (try-move w 1 0)
                    (gc/get-square 1 1))
                w)
           ; square already occupied
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 1 0 w)
                    (try-move w 1 0))
                nil)
           ; places new piece in top left corner and captures south and south-east
           (is= (-> (gc/create-game 4 4)
                    (gc/set-player-in-turn b)
                    (gc/set-square 0 0 b)
                    (gc/set-square 3 0 b)
                    (gc/set-square 0 1 w)
                    (gc/set-square 0 2 w)
                    (try-move b 0 3)
                    (gc/get-squares))
                [b 0 0 0
                 b b b 0
                 b b b 0
                 b 0 0 b]))}
  [state player x y]
  (let [captures (captures state player x y)]
    (if (and (= player (gc/get-player-in-turn state))
             (not (occupied state x y))
             (not-empty captures))
      (-> (gc/set-square state x y player)
          (gc/set-squares captures player))
      nil)))
