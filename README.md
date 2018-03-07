# [JS-Workshop](https://github.com/ClojureTO/JS-Workshop)

# Part 2: re-frame

See [master branch](https://github.com/ClojureTO/JS-Workshop) for Part 1.

## Re-framing the application

Now that we've seen how basic Reagent works, let's take a look at adding some structure to our code. Re-frame is a framework built on top of Reagent that helps manage the state of the application.

### Re-frame core concepts

Re-frame uses a single atom to represent the state of the application. This atom is used internally by re-frame, and we don't interact with it directly. Instead, we use dispatchers to update the state of the atom, and subscriptions to observe it.

Essentially, re-frame follows the MVC approach to structuring the UI code. The model is modified using dispatchers, and the view observes it via subscriptions. Let's take a look at how this works in practice.

Our current version of the application uses Reagent reactive atoms to track the state of the data and update the UI components as the state of the data changes.

When using re-frame, we will dispatch events whenever we wish to update the state, and subscribe to changes in our components to observe the data. Let's take a look at what event handlers and subscriptions look like.

#### re-frame event handlers

Event handlers are defined using the `re-frame.core/reg-event-db` function. The function accepts a keyword used to uniquely identify the event, and a function that will be triggered when the event is dispatched. Let's take a look at an example handler:

```clojure
(re-frame.core/reg-event-db
 :set-value
 (fn [db [event-id value]]
   (assoc db :value value)))
```

We now have an handler associated with the `:set-value` event. The event handling function accepts two arguments. The first argument is the current state of the re-frame atom, and the second is the vector of arguments passed to the event. The first element of the arguments vector will be the event id, in this case `:set-value`, followed by zero or more optional arguments.

Now that the event has been defined, let's take a look at how we dispatch it. This is done using the `re-frame.core/dispatch` function:

```clojure
(re-frame.core/dispatch [:set-value "some value"])
```

The above code will trigger the `:set-value` event, and the vector `[:set-value "some value"]` will be passed to the event handler function.

 The function will associate the `:value` key in the `db` with the value that was passed in. In our case, the value will become the string `"some value"`.

Now that we've seen how to create an event handler to update the re-frame database, let's take a look at how we can subscribe to views inside it.

#### re-frame subscriptions

Subscriptions are created using the `re-frame.core/reg-sub` function. This function has similar semantics to the `reg-event-db` function. Let's look at a concrete example of a subscription to a key in the re-frame atom below:

```clojure
(re-frame.core/reg-sub
 :view-key
 (fn [db [event-id k]]
   (get db k)))
```

Once again, the function accepts an identifier followed by the handler function. The handler function accepts the current state of the atom, followed by a vector of arguments.

To create a subscription to the `:value` key we set earlier, we use the `re-frame.core/subscribe` function:

```clojure
(re-frame.core/subscribe [:view-key :value])
```

The subscription returns a Reagent reaction that contains the computation for the subscription. This reaction will only be evaluated when the state of the database changes. In order to get the value from the reaction, we need to dereference it as we would with a Reagent atom:

```clojure
[:p @(re-frame.core/subscribe [:view-key :value])]
```

That's all we need to know about re-frame to update our application. We'rew now ready to take a look at how we can update the project to use it.

### Adding re-frame dependency

First thing we'll need to do is to add the re-frame dependency in the `project.clj` file:

```clojure
:dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.671" :scope "provided"]
                 [reagent "0.7.0"]
                 [re-frame "0.9.4"]
                 [cljsjs/chartjs "2.5.0-0"]
                 [cljs-ajax "0.6.0"]]
```

Once that's done, we'll have to restart the application for the new dependency to be loaded.

### Separating events from the UI

Currently, the business logic of our application is mixed with the UI code in the `reddit-viewer.core` namespace. Let's start by identifying events in the code so that we can split them out. Our application has three main events in it:

* load posts from Reddit
* sort posts by comments or by score
* select view to display post previews or the chart

We'll now create a namespace called `reddit-viewer.events` in a file called `src/reddit_viewer/controllers.cljs`. The namespace declaration will look as follows:

```clojure
(ns reddit-viewer.controllers
  (:require
    [ajax.core :as ajax]
    [re-frame.core :as rf]))
```

### Adding events

We're now ready to add the first event handler. It will be used to initialized the re-frame atom when the application loads. This atom is typically referred to as the re-frame database.

```clojure
(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    {:view     :posts
     :sort-key :score}))
```

Since we're initializing the application, the handler function doesn't need use the arguments that are passed in. It simply returns a map representing the default state:

```clojure
{:view     :posts
 :sort-key :score}
```

#### Task 1: load posts

Next, we'll create an event to populate the posts in the re-frame db:

```clojure
(defn find-posts-with-preview [posts]
  (filter #(= (:post_hint %) "image") posts))

(rf/reg-event-db
  :set-posts
  (fn [db [_ posts]]
    (assoc db :posts
              (->> (get-in posts [:data :children])
                   (map :data)
                   (find-posts-with-preview)))))
```

This is essentially the same code we used in the `load-posts` function earlier. The only difference is that instead of setting the value in the `posts` atom, we're now associating it as the `:posts` key on the re-frame db.

Next, we'll create an event to do the Ajax call that will load the posts. This action requires a side effect that will asynchronously call the remote service to fetch the data.
It's good practice to distinguish between actions that modify the state of the re-frame database and those that trigger side effects. The mechanism that re-frame provides for this is the
[effectful handler](https://github.com/Day8/re-frame/blob/master/docs/EffectfulHandlers.md). We'll register an effect for doing Ajax calls with `reg-fx`, and an event to trigger it with
`reg-event-fx` as seen below:

```clojure
(rf/reg-fx
 :ajax-get
 (fn [[url handler]]
   (ajax/GET url
             {:handler         handler
              :response-format :json
              :keywords?       true})))

(rf/reg-event-fx
  :load-posts
  (fn [_ [_ url]]
    {:ajax-get [url #(rf/dispatch [:set-posts %])]}))
```

The `:load-posts` event returns a map where the keys point to the effects and the values represent their parameters.
In this case we're dispatching a single effect that will call the specified URL via Ajax and run the handler function when it receives the response.
The handler function will dispatch the `:set-posts` event to set the post data in the database.

>Exercise: modify the application to create a loading notification while the posts are being fetched.

#### Task 2: sort posts

The event to sort posts will accept a `sort-key` as its parameter and sort the posts using it:

```clojure
(rf/reg-event-db
  :sort-posts
  (fn [db [_ sort-key]]
    (update db :posts (partial sort-by sort-key >))))
```

#### Task 3: select view

Finally, we need an event to select the current view:

```clojure
(rf/reg-event-db
  :select-view
  (fn [db [_ view]]
    (assoc db :view view)))
```

This event simply associates the `:view` key in the `db` with the value that we pass in.


### Adding subscriptions

This takes care of all the events needed for our application. We now need to add a couple of subscriptions to observe the state of the re-frame database:

```clojure
(rf/reg-sub
  :view
  (fn [db _]
    (:view db)))

(rf/reg-sub
  :posts
  (fn [db _]
    (:posts db)))
```

This provides us with the ability to access the selected view and the collection of posts populated in the database.


### Updating the UI

With all that in place, let's navigate to the `reddit-viewer.core` namespace and update it to use the events we created. We'll update the namespace declaration to require the necessary namespaces:

```clojure
(ns reddit-viewer.core
  (:require
    [ajax.core :as ajax]
    [reagent.core :as r]
    [reddit-viewer.chart :as chart]
    [reddit-viewer.controllers]
    [re-frame.core :as rf]))
```

Next, we'll update the `init!` function to initialize the re-frame database on startup and to trigger the event for loading posts:

```clojure
(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (rf/dispatch [:load-posts "http://www.reddit.com/r/Catloaf.json?sort=new&limit=50"])
  (mount-root))
```

Here, we're calling the `rf/dispatch-sync` function to trigger the `:initialize-db` event. This is a blocking version of re-frame dispatch that ensures that the event finishes before the next statement is called. This is necessary to ensure that the re-frame database is initialized before we start using it.

We'll update the `home-page` function to subscribe to the selected view using `@(rf/subscribe [:view])`, and we'll pass the subscription to the posts to the `dsiplay-posts` function: `[display-posts @(rf/subscribe [:posts])]`:

```clojure
(defn home-page []
  (let [view @(rf/subscribe [:view])]
    [:div
     [navbar view]
     [:div.card>div.card-block
      [:div.btn-group
       [sort-posts "score" :score]
       [sort-posts "comments" :num_comments]]
      (case view
        :chart [chart/chart-posts-by-votes]
        :posts [display-posts @(rf/subscribe [:posts])])]]))
```

The `sort-posts` component will need to change as well since it will now be dispatching the event to set the sort key:

```clojure
(defn sort-posts [title sort-key]
  [:button.btn.btn-secondary
   {:on-click #(rf/dispatch [:sort-posts sort-key])}
   (str "sort posts by " title)])
```

Finally, the `navitem` function will need to be updated to dispatch the `:select-view` event:

```clojure
(defn navitem [title view id]
  [:li.nav-item
   {:class-name (when (= id view) "active")}
   [:a.nav-link
    {:href     "#"
     :on-click #(rf/dispatch [:select-view id])}
    title]])
```

We can now remove the following code as it's no longer used:

```clojure
(defonce posts (r/atom nil))

(defn find-posts-with-preview [posts]
  (filter #(= (:post_hint %) "image") posts))

(defn load-posts []
  (ajax/GET "http://www.reddit.com/r/Catloaf.json?sort=new&limit=10"
            {:handler         #(->> (get-in % [:data :children])
                                    (map :data)
                                    (find-posts-with-preview)
                                    (reset! posts))
             :response-format :json
             :keywords?       true}))
```

We now have clear separation between the logic in the `redditviewer.events` and the UI logic in the `reddit-viewer.core`.

One last thing left to do is to update the `reddit-viewer.chart` namespace to subscribe to the posts:

```clojure
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
```
