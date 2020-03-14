(ns othello.game.api
  (:require
    [ysera.test :refer [is= is is-not]]
    [ysera.error :refer [error]]
    [othello.game.state :as gs]
    [othello.game.logic :as gl]
    [othello.game.ai :as ai]))

(defn play
  [state i j]
  (let [new-state (gl/try-move state (gs/get-player-in-turn state) i j)]
          (if new-state
            (do (println "play: valid move")
                (if (nil? (gl/winner new-state))
                  (ai/ai-play new-state)
                  new-state))
            (do (println "play: invalid move")
                state))))

(defn restart []
  (gs/create-game))

(defn set-difficulty
  [state difficulty]
  (if (pos-int? difficulty)
    (println "set-difficulty: valid")
    (println "set-difficulty: invalid"))
  (if (pos-int? difficulty)
    (gs/set-ai-difficulty state difficulty)
    state))
