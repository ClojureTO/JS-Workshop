(ns reddit-viewer.prod
  (:require [reddit-viewer.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
