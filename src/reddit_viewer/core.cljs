(ns reddit-viewer.core
  (:require
    [ajax.core :as ajax]
    [baking-soda.core :as b]
    [reagent.core :as r]
    [reddit-viewer.chart :as chart]))

(defonce posts (r/atom nil))

(defn load-posts []
  (ajax/GET "http://www.reddit.com/r/Catloaf.json?sort=new&limit=10"
            {:handler         #(->> (get-in % [:data :children])
                                    (map :data)
                                    (reset! posts))
             :response-format :json
             :keywords?       true}))

(defn find-posts-with-preview [posts]
  (filter #(= (:post_hint %) "image") posts))

;; -------------------------
;; Views

(defn display-post [{:keys [permalink subreddit title score url]}]
  [b/Card
   {:class "m-2"}
   [b/CardBlock
    [b/CardTitle
     [:a {:href (str "http://reddit.com" permalink)} title " "]]
    [:div [b/Badge {:color "info"} subreddit " score " score]]
    [:img {:width "300px" :src url}]]])

(defn display-posts [posts]
  (when-not (empty? posts)
    [:div
     (for [posts-row (->> posts (find-posts-with-preview) (partition-all 3))]
       ^{:key posts-row}
       [:div.row
        (for [post posts-row]
          ^{:key post}
          [:div.col-4 [display-post post]])])]))

(defn sort-posts [title sort-key]
  (when-not (empty? @posts)
    [b/Button
     {:on-click #(swap! posts (partial sort-by sort-key))}
     (str "sort posts by " title)]))

(defn navitem [title view id]
  [b/NavItem
   {:className (when (= id @view) "active")}
   [b/NavLink
    {:href     "#"
     :on-click #(reset! view id)}
    title]])

(defn navbar [view]
  [b/Navbar
   {:className "navbar-toggleable-md navbar-light bg-faded"}
   [b/Nav
    {:className "navbar-nav mr-auto"}
    [navitem "Posts" view :posts]
    [navitem "Chart" view :chart]]])

(defn home-page []
  (r/with-let [view (r/atom :posts)]
    [:div
     [navbar view]
     [b/Card
      [b/CardBlock
       [b/ButtonGroup
        [sort-posts "score" :score]
        [sort-posts "comments" :num_comments]]
       (case @view
         :chart [chart/chart-posts-by-votes posts]
         :posts [display-posts @posts])]]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root)
  (load-posts))
