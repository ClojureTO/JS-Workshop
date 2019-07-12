(ns reddit-viewer.chart
  (:require
    ["chart.js" :as chartjs]
    [reagent.core :as r]))

(defn render-data [node data]
  (chartjs/Chart.
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

(defn render-chart [chart data]
  (fn [component]
    (when-not (empty? @data)
      (let [node (r/dom-node component)]
        (destroy-chart chart)
        (reset! chart (render-data node @data))))))

(defn chart-posts-by-votes [data]
  (let [chart (r/atom nil)]
    (r/create-class
      {:component-did-mount  (render-chart chart data)
       :component-did-update (render-chart chart data)
       :component-will-unmount (fn [_] (destroy-chart chart))
       :render               (fn [] (when @data [:canvas]))})))
