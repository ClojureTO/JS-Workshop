(ns ^:figwheel-no-load reddit-viewer.dev
  (:require [reddit-viewer.core :as core]
            [devtools.core :as devtools]
            [figwheel.client :as figwheel :include-macros true]))

(devtools/install!)

(enable-console-print!)

(core/init!)
