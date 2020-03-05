(ns othello.game.api
  (:require
    [ysera.test :refer [is= is is-not]]
    [ysera.error :refer [error]]
    [othello.game.core :as gc]
    [othello.game.logic :as gl]))

(defn play
  [state x y]
  (let [move (gl/try-move state (gc/get-player-in-turn state) x y)]
    (if move
      (do (println "play: valid move")
          (gl/change-player-in-turn move))
      (do (println "play: invalid move")
          state))))
