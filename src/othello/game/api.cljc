(ns othello.game.api
  (:require
    [ysera.test :refer [is= is is-not]]
    [ysera.error :refer [error]]
    [othello.game.core :as gc]
    [othello.game.logic :as gl]))

(defn play
  [state i j]
  (let [move (gl/try-move state (gc/get-player-in-turn state) i j)]
    (if move
      (do (println "play: valid move")
          move)
      (do (println "play: invalid move")
          state))))

(defn restart []
  (gc/create-game))
