(ns othello.prod
  (:require
    [othello.main :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
