(ns othello.game.core
  (:require
    [ysera.test :refer [is= is is-not error?]]
    [ysera.error :refer [error]]))

(def w 1)
(def b 2)

(defn get-board
  [state]
  (:board state))

(defn set-board
  [state board]
  (assoc state :board board))

(defn get-board-height
  [state]
  (count (get-board state)))

(defn get-board-width
  [state]
  (count (get (get-board state) 0)))

(defn get-player-in-turn
  [state]
  (:player-in-turn state))

(defn set-player-in-turn
  [state player]
  (assoc state :player-in-turn player))

(defn get-square
  [state i j]
  (get (get (get-board state) i) j))

(defn set-square
  [state i j value]
  (assoc-in state [:board i j] value))

(defn set-squares
  "Accepts a vector of coordinates and sets all the corresponding squares to value."
  [state indices value]
  (reduce
    (fn [state [i j]]
      (set-square state i j value))
    state
    indices))

(defn create-empty-board
  {:test (fn []
           (is= (create-empty-board 4 4)
                [[0 0 0 0]
                 [0 0 0 0]
                 [0 0 0 0]
                 [0 0 0 0]]))}
  [width height]
  (when (or (odd? height) (odd? width))
    (error "Board dimensions have to be even numbers."))
  (into [] (repeat height (into [] (repeat width 0)))))

(defn set-starting-pieces
  "Sets the four middle squares to alternating black/white."
  [state]
  (let [middle-i (/ (get-board-height state) 2)
        middle-j (/ (get-board-width state) 2)]
    (-> state
        (set-square (- middle-i 1) (- middle-j 1) w)
        (set-square (- middle-i 1) middle-j b)
        (set-square middle-i (- middle-j 1) b)
        (set-square middle-i middle-j w))))

(defn create-game
  {:test (fn []
           (is= (create-game 4 4)
                {:player-in-turn w
                 :board          [[0 0 0 0]
                                  [0 w b 0]
                                  [0 b w 0]
                                  [0 0 0 0]]})
           (is= (create-game 6 4)
                {:player-in-turn w
                 :board          [[0 0 0 0 0 0]
                                  [0 0 w b 0 0]
                                  [0 0 b w 0 0]
                                  [0 0 0 0 0 0]]})
           (is= (create-game)
                {:player-in-turn w
                 :board          [[0 0 0 0 0 0 0 0]
                                  [0 0 0 0 0 0 0 0]
                                  [0 0 0 0 0 0 0 0]
                                  [0 0 0 w b 0 0 0]
                                  [0 0 0 b w 0 0 0]
                                  [0 0 0 0 0 0 0 0]
                                  [0 0 0 0 0 0 0 0]
                                  [0 0 0 0 0 0 0 0]]})
           (is= (create-game [[0 w]
                              [b 0]])
                {:player-in-turn w
                 :board          [[0 w]
                                  [b 0]]}))}
  ([board-width board-height]
   (-> {:board          (create-empty-board board-width board-height)
        :player-in-turn w}
       (set-starting-pieces)))
  ([]
   (-> {:board          (create-empty-board 8 8)
        :player-in-turn w}
       (set-starting-pieces)))
  ([board]
   {:board          board
    :player-in-turn w}))
