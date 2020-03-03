(ns othello.game.logic
  (:require
    [ysera.test :refer [is= is is-not]]
    [ysera.error :refer [error]]
    [othello.game.core :as gc :refer [w b]]))

(def get-opponent {w b
                   b w})

(defn count-flips-helper
  "Recursively counts the amount of flips in a certain direction playing a piece
  at coordinates x y would cause. The direction is decided by dx and dy (deltas)."
  [state player dx dy x y count]
  {:pre [(contains? #{1 0 -1} dx) (contains? #{1 0 -1} dy)]}
  (let [square (gc/get-square state x y)
        opponent (get-opponent player)]
    (cond
      (= square opponent) (count-flips-helper state player dx dy (+ x dx) (+ y dy) (+ count 1))
      (and (= square player) (> count 0)) count
      :else nil)))

(defn count-north-flips
  "Returns the amount of flips in the north direction playing a piece
  at coordinates x y would cause, nil if none."
  {:test (fn []
           ; valid flip for white
           (is= (-> (gc/create-game 4 4)
                    (count-north-flips w 1 0))
                1)
           ; but not for black
           (is= (-> (gc/create-game 4 4)
                    (count-north-flips b 1 0))
                nil)
           ; going out of bounds
           (is= (-> (gc/create-game 4 4)
                    (count-north-flips w 2 3))
                nil)
           ; multiple flips
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 0 3 w)
                    (gc/set-square 0 2 b)
                    (gc/set-square 0 1 b)
                    (count-north-flips w 0 0))
                2))}
  [state player x y]
  (count-flips-helper state player 0 1 x (+ y 1) 0))

(defn count-east-flips
  {:test (fn []
           (is= (-> (gc/create-game 4 4)
                    (count-east-flips w 0 1))
                1))}
  [state player x y]
  (count-flips-helper state player 1 0 (+ x 1) y 0))

(defn count-south-flips
  {:test (fn []
           (is= (-> (gc/create-game 4 4)
                    (count-south-flips w 2 3))
                1))}
  [state player x y]
  (count-flips-helper state player 0 -1 x (- y 1) 0))

(defn count-west-flips
  {:test (fn []
           (is= (-> (gc/create-game 4 4)
                    (count-west-flips w 3 2))
                1))}
  [state player x y]
  (count-flips-helper state player -1 0 (- x 1) y 0))

(defn count-north-east-flips
  {:test (fn []
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 3 3 w)
                    (gc/set-square 2 2 b)
                    (gc/set-square 1 1 b)
                    (count-north-east-flips w 0 0))
                2))}
  [state player x y]
  (count-flips-helper state player 1 1 (+ x 1) (+ y 1) 0))

(defn count-south-east-flips
  {:test (fn []
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 3 0 w)
                    (gc/set-square 2 1 b)
                    (gc/set-square 1 2 b)
                    (count-south-east-flips w 0 3))
                2))}
  [state player x y]
  (count-flips-helper state player 1 -1 (+ x 1) (- y 1) 0))

(defn count-south-west-flips
  {:test (fn []
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 0 0 w)
                    (gc/set-square 1 1 b)
                    (gc/set-square 2 2 b)
                    (count-south-west-flips w 3 3))
                2))}
  [state player x y]
  (count-flips-helper state player -1 -1 (- x 1) (- y 1) 0))

(defn count-north-west-flips
  {:test (fn []
           (is= (-> (gc/create-game 4 4)
                    (gc/set-square 0 3 w)
                    (gc/set-square 1 2 b)
                    (gc/set-square 2 1 b)
                    (count-north-west-flips w 3 0))
                2))}
  [state player x y]
  (count-flips-helper state player -1 1 (- x 1) (+ y 1) 0))

(defn valid-move?                                           ;TODO
  [state player x y])

(defn valid-moves                                           ;TODO
  [state player])
