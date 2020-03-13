(ns othello.all
  (:require [clojure.test :refer [run-all-tests successful?]]
            [ysera.test :refer [deftest is]]
            [othello.game.core]
            [othello.game.logic]
            [othello.game.api]
            [othello.ai.minimax]))

(deftest test-all (is (successful? (time (run-all-tests #"othello\.(?!all).*")))))
