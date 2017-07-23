(ns reddit-viewer.events
  (:require
    [ajax.core :as ajax]
    [re-frame.core :as rf]))

(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    {:view     :posts
     :sort-key :score}))

(defn find-posts-with-preview [posts]
  (filter #(= (:post_hint %) "image") posts))

(rf/reg-event-db
  :set-posts
  (fn [db [_ posts]]
    (assoc db :posts
              (->> (get-in posts [:data :children])
                   (map :data)
                   (find-posts-with-preview)))))

(rf/reg-event-db
  :load-posts
  (fn [db _]
    (ajax/GET "http://www.reddit.com/r/Catloaf.json?sort=new&limit=50"
              {:handler         #(rf/dispatch [:set-posts %])
               :response-format :json
               :keywords?       true})
    db))

(rf/reg-event-db
  :sort-posts
  (fn [db [_ sort-key]]
    (update db :posts (partial sort-by sort-key >))))

(rf/reg-event-db
  :select-view
  (fn [db [_ view]]
    (assoc db :view view)))

(rf/reg-sub
  :view
  (fn [db _]
    (:view db)))

(rf/reg-sub
  :posts
  (fn [db _]
    (:posts db)))


