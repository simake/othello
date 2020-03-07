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
  The direction is determined by di and dj (row and column deltas)."
  {:test (fn []
           ; white captures northward
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction w 3 1 -1 0))
                [[2 1]])
           ; east
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction w 2 0 0 1))
                [[2 1]])
           ; and southwest
           (is= (-> (gc/create-game [[0 0 0 0]
                                     [0 0 b 0]
                                     [0 b 0 0]
                                     [w 0 0 0]])
                    (captures-in-direction w 0 3 1 -1))
                [[1 2] [2 1]])
           ; white would capture but not black
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction b 3 1 -1 0))
                [])
           ; black captures westward
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction b 2 3 0 -1))
                [[2 2]])
           ; immediately going out of bounds
           (is= (-> (gc/create-game 4 4)
                    (captures-in-direction w 0 2 -1 0))
                []))}
  [state player i j di dj]
  {:pre [(contains? #{1 0 -1} di) (contains? #{1 0 -1} dj)]}
  (loop [i (+ i di)
         j (+ j dj)
         captures []]
    (let [square (gc/get-square state i j)
          opponent (get-opponent player)]
      (cond
        (= square opponent) (recur (+ i di) (+ j dj) (conj captures [i j]))
        (and (= square player) (not-empty captures)) captures
        :else []))))

(defn captures
  "Returns the captures from playing a piece at this position."
  {:test (fn []
           ; south-east and south (white)
           (is= (-> (gc/create-game [[0 b 0 w]
                                     [b b 0 0]
                                     [b 0 b 0]
                                     [w 0 0 w]])
                    (captures w 0 0)
                    (sort))
                [[1 0] [1 1] [2 0] [2 2]])
           ; west, north-west, and north (black)
           (is= (-> (gc/create-game [[w 0 0 0]
                                     [0 b 0 b]
                                     [0 0 w w]
                                     [b w w 0]])
                    (captures b 3 3)
                    (sort))
                [[2 2] [2 3] [3 1] [3 2]])
           ; no captures
           (is= (-> (gc/create-game 4 4)
                    (captures b 0 0))
                []))}
  [state player i j]
  (let [directions
        (for [di [1 0 -1]
              dj [1 0 -1]
              :when (not= di dj 0)]
          [di dj])]
    (reduce
      (fn [acc direction]
        (into [] (concat acc (apply captures-in-direction state player i j direction))))
      [] directions)))

(defn occupied
  "Returns the player on a square, nil if it isn't occupied."
  [state i j]
  (let [player (gc/get-square state i j)]
    (if (or (= player w) (= player b))
      player
      nil)))

(defn try-move
  "Attempts a move. Returns new state upon success, nil otherwise."
  {:test (fn []
           ; places new piece
           (is= (-> (gc/create-game 4 4)
                    (try-move w 3 1)
                    (gc/get-square 3 1))
                w)
           ; captures piece
           (is= (-> (gc/create-game 4 4)
                    (try-move w 3 1)
                    (gc/get-square 2 1))
                w)
           ; square already occupied
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 3 1 w)
                    (try-move w 3 1))
                nil)
           ; places new piece in top left corner and captures south and south-east
           (is= (-> (gc/create-game [[0 w w w]
                                     [w w 0 0]
                                     [b 0 w 0]
                                     [0 0 0 b]])
                    (gc/set-player-in-turn b)
                    (try-move b 0 0)
                    (gc/get-board))
                [[b w w w]
                 [b b 0 0]
                 [b 0 b 0]
                 [0 0 0 b]])
           ; north only
           (is= (-> (gc/create-game [[0 0 0 b]
                                     [0 w b w]
                                     [w w w 0]
                                     [0 0 0 0]])
                    (gc/set-player-in-turn b)
                    (try-move b 2 3)
                    (gc/get-board))
                [[0 0 0 b]
                 [0 w b b]
                 [w w w b]
                 [0 0 0 0]]))}
  [state player i j]
  (let [captures (captures state player i j)]
    (if (and (= player (gc/get-player-in-turn state))
             (not (occupied state i j))
             (not-empty captures))
      (-> (gc/set-square state i j player)
          (gc/set-squares captures player))
      nil)))
