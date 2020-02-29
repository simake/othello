(ns othello.game
  (:require
    [ysera.test :refer [is= is is-not]]
    [ysera.error :refer [error]]))

(def w 1)
(def b 2)

(defn create-empty-board
  {:test (fn []
           (is= (create-empty-board 4 4)
                {:width   4
                 :height  4
                 :squares [0 0 0 0
                           0 0 0 0
                           0 0 0 0
                           0 0 0 0]}))}
  [width height]
  (when (or (odd? height) (odd? width))
    (error "Board dimensions have to be even numbers."))
  {:width   width
   :height  height
   :squares (vec (repeat (* width height) 0))})

(defn create-empty-state
  [board-width board-height]
  {:board       (create-empty-board board-width board-height)
   :whites-turn true})

(defn get-board
  [state]
  (:board state))

(defn get-board-height
  [state]
  (:height (get-board state)))

(defn get-board-width
  [state]
  (:width (get-board state)))

(defn get-squares
  [state]
  (:squares (get-board state)))

(defn coordinates-to-vector-index
  "Converts coordinates in the xy-plane to a vector index."
  {:test (fn []
           (is= (coordinates-to-vector-index 2 2 0 0) 2)
           (is= (coordinates-to-vector-index 3 3 1 1) 4)
           (is= (coordinates-to-vector-index 3 1 2 0) 2))}
  [board-width board-height x y]
  (+ (* (- board-height y 1) board-width) x))

(defn get-square
  [state x y]
  (let [board (get-board state)
        index (coordinates-to-vector-index (:width board) (:height board) x y)]
    (nth (:squares board) index)))

(defn set-square
  [state x y value]
  (let [board (get-board state)
        index (coordinates-to-vector-index (:width board) (:height board) x y)]
    (update-in state [:board :squares] #(assoc % index value))))

(defn set-starting-pieces
  "Sets the four middle squares to alternating black/white."
  [state]
  (let [height (get-board-height state)
        width (get-board-width state)
        middle-y (/ height 2)
        middle-x (/ width 2)]
    (-> state
        (set-square (- middle-x 1) middle-y w)
        (set-square middle-x middle-y b)
        (set-square (- middle-x 1) (- middle-y 1) b)
        (set-square middle-x (- middle-y 1) w))))

(defn create-game
  {:test (fn []
           (is= (create-game 4 4)
                {:whites-turn true
                 :board       {:width   4
                               :height  4
                               :squares [0 0 0 0
                                         0 w b 0
                                         0 b w 0
                                         0 0 0 0]}})
           (is= (create-game 6 4)
                {:whites-turn true
                 :board       {:width   6
                               :height  4
                               :squares [0 0 0 0 0 0
                                         0 0 w b 0 0
                                         0 0 b w 0 0
                                         0 0 0 0 0 0]}})
           (is= (create-game)
                {:whites-turn true
                 :board       {:width   8
                               :height  8
                               :squares [0 0 0 0 0 0 0 0
                                         0 0 0 0 0 0 0 0
                                         0 0 0 0 0 0 0 0
                                         0 0 0 w b 0 0 0
                                         0 0 0 b w 0 0 0
                                         0 0 0 0 0 0 0 0
                                         0 0 0 0 0 0 0 0
                                         0 0 0 0 0 0 0 0]}}))}
  ([board-width board-height]
   (-> (create-empty-state board-width board-height)
       (set-starting-pieces)))
  ([]
   (-> (create-empty-state 8 8)
       (set-starting-pieces))))
