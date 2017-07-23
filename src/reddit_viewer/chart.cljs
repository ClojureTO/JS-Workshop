(ns reddit-viewer.chart
  (:require
    [cljsjs.chartjs]
    [reagent.core :as r]
    [re-frame.core :as rf]))

(defn render-data [node data]
  (js/Chart.
    node
    (clj->js
      {:type    "bar"
       :data    {:labels   (map :title data)
                 :datasets [{:label "votes"
                             :data  (map :score data)}]}
       :options {:scales {:xAxes [{:display false}]}}})))

(defn destroy-chart [chart]
  (when @chart
    (.destroy @chart)
    (reset! chart nil)))

(defn render-chart [chart]
  (fn [component]
    (when-let [posts @(rf/subscribe [:posts])]
      (destroy-chart chart)
      (reset! chart (render-data (r/dom-node component) posts)))))

(defn render-canvas []
  (when @(rf/subscribe [:posts]) [:canvas]))

(defn chart-posts-by-votes [data]
  (let [chart (atom nil)]
    (r/create-class
      {:component-did-mount    (render-chart chart)
       :component-did-update   (render-chart chart)
       :component-will-unmount (fn [_] (destroy-chart chart))
       :render                 render-canvas})))
