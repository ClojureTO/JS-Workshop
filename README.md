# [JS-Workshop](https://github.com/ClojureTO/JS-Workshop)

## Before the Workshop

Please make sure that you have a copy of the JDK and Leiningen build tool setup to follow along with the workshop.
You can follow installation instructions in the links below:

* [JDK 1.8+](http://www.azul.com/downloads/zulu/)
* [Leiningen](https://leiningen.org/)

### Creating and running the project

Run the following commands to create a new project and run it to ensure that the setup was completed successfully:

    lein new reagent-frontend reddit-viewer
    cd reddit-viewer
    lein figwheel

If the project starts up successfully, then you should have a browser window open at `localhost:3449/index.html`.

## During the Workshop

This is a comprehensive guide to the workshop itself, for those playing along from home!

We'll update project dependencies in `project.clj` to look as follows:

```clojure
:dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.671" :scope "provided"]
                 [reagent "0.7.0"]
                 [cljsjs/chartjs "2.5.0-0"]
                 [cljs-ajax "0.6.0"]]
```

Next, let's replace the generated CSS link with the Bootstrap CSS in the `public/index.html` file:

```xml
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1" name="viewport">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
</head>
```

start the project in development mode:

    lein figwheel

Leiningen will download the dependencies and start compiling the project, this can take a minute first time around.
Once the project compilation finishes, a browser window will open at [http://localhost:3449/index.html](http://localhost:3449/index.html).

## Editing the project

Now that we have the project running, let's see how we can add some functionality to it.
We'll open up the `reddit_viewer/core.cljs` file that has some initial boilerplate in it and see what it's doing.

```clojure
(ns reddit-viewer.core
    (:require
      [reagent.core :as r]))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
```

The top section of the file contains a namespace declaration. The namespace requires the `reagent.core` namespace that's
used to create the UI.

The `home-page` function creates a Reagent component. The component contains a `div` with an `h2` tag inside it.

Reagent uses Clojure literal notation for vectors and maps to represent HTML. The tag is defined using a vector, where the first element is the keyword representing the tag name, followed by an optional map of attributes, and the tag content.

For example, `[:div [:h2 "Welcome to Reagent"]` maps to `<div><h2>Welcome to Reagent</h2></div>`. If we wanted to add `id` and `class` to the `div`, we could do that as follows: `[:div {:id "foo" :class "bar baz"} ...]`.

Since setting the `id` and `class` attributes is a very common operation, Reagent provides a shortcut for doing that using syntax similar to CSS selectors: `[:div#foo.bar.baz ...]`.

This component is rendered inside the DOM element with the ID `app`. This element is defined in the `public/index.html` file
by the `mount-root` function.

Finally, we have the `init!` function that serves as the entry point for the application.

### Task 1: Loading data using Ajax and viewing it.

Let's start by creating a container to hold the results:

```clojure
(defonce posts (r/atom nil))
```

The `atom` is a container for mutable data. We'll initialize it with a `nil` value.

Next, we'll require the `ajax.core` namespace and add a couple of functions that will load posts from the `http://www.reddit.com/r/Catloaf.json?sort=new&limit=9` URL, filter out the ones with images,
and save them in the `posts` atom:

```clojure
(ns reddit-viewer.core
    (:require
      [ajax.core :as ajax]
      [reagent.core :as r]))

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

The `load-posts` function loads the JSON data and converts it to a Clojure data structure. We pass the `ajax/GET` function the URL and
a map of options. The options contain the `:handler` key pointing to the function that should be called to handle the successful response,
the `:response-format` key that hints that the response type is JSON, and `:keywords?` hint indicating that we would like to convert JSON
string keys into Clojure keywords for maps.

The original data has the following structure:

```clojure
{:data {:children [{:data {...}} ...]}}
```

The top level data structure is a map that contains a key called `:data`, this key points to a map that contains a key called
`:children`. Finally, the `:children` key points to a collection of maps representing the posts. Each map, in turn, has a key
 called `:data` that contains the data for the post.

Our `:handler` function grabs the collection of posts, and maps across them to get the `:data` key containing the information about
each post. It then calls the `find-posts-with-preview` function to filter out posts without images. After we process the original response data, we reset the `posts` atom with the result.

We can test our function in the Figwheel REPL by running the following commands:

```clojure
(in-ns 'reddit-viewer.core)
(load-posts)
(first @posts)
```

We should see the data contained in the first item in the collection of posts that was loaded.

### Task 2: Rendering the data

Each post map contains a `:url` key that points to an image. Let's write a component function to render the image from the first post that looks as follows:

```clojure
(defn display-post [{:keys [url]}]
  (when url [:img {:src url}]))
```

When a Reagent component function returns `nil` it is omitted in the DOM, so the `display-posts` component will only be rendered when provided with a map containing a `:url` key that has a value.

We can now parent this component under the `home-page` component:

```clojure
(defn home-page []
  [:div [:h2 "Welcome to Reagent"]
   [display-post (first @posts)]])
```

Note that we're putting the `display-post` component in a vector `[display-post]` as opposed to calling it as a function with `(display-post)`.

This is a property of how the Reagent library works. The templates specify the structure of the page. Reagent
then manages the lifecycle of the component functions, and decides when they need to be called based on the state of the data.

If we called the function directly by writing `(display-post)`, then it would be executed a single time when the code is initialized, and
it would not be repainted when the contents of `posts` atom change.

By using the vector notation and writing `[display-post]`, we're telling Reagent where we would like to render the `display-post` component, and let it manage when to call it based on the state of the data.

Reagent atoms are reactive meaning that any time the atom is dereferenced using the `@` notation, a listener is created. When the atom value changes, all the listeners are notified of the change, and the components are repainted.

We can tests this by going to the REPL and clearing the `posts` atom:

```clojure
(reset! posts nil)
```

We can see that the image disappears on the page once the contents of the atom have been cleared. Let's run the `(load-posts)` function again:

```clojure
(load-posts)
```

We should be seeing the cat picture once again as the `display-post` component is repainted with new data.

#### Working with HTML

We've now seen that the data is being loaded, but it's not terribly nice to look at. Let's render it in a better way using Bootstrap CSS.
We'll update the `display-post` component function as follows:

```clojure
(defn display-post [{:keys [permalink subreddit title score url]}]
  [:div.card.m-2
   [:div.card-block
    [:h4.card-title
     [:a {:href (str "http://reddit.com" permalink)} title " "]]
    [:div [:span.badge.badge-info {:color "info"} subreddit " score " score]]
    [:img {:width "300px" :src url}]]])
```

Now that we can render a single post nicely, let's write a function that will render a multiple posts:

 ```clojure
 (defn display-posts [posts]
   (when-not (empty? posts)
     [:div
      (for [posts-row (partition-all 3 posts)]
        ^{:key posts-row}
        [:div.row
         (for [post posts-row]
           ^{:key post}
           [:div.col-4 [display-post post]])])]))
```

The function will accept a collection of posts as its parameter. It will then check whether the collection is empty.

When the `posts` are not empty, we'll partition them into groups of three.
We'll create a Bootstrap row for each group and pass the posts in the row to the `display-post` function we wrote earlier.

Note that we're using the `^{:key posts-row}` notation for dynamic collections elements. This provides Reagent with a unique identifier for each element to decide when to repaint it efficiently. If the key was omitted, then Reagent would repaint all elements whenever any of the elements need repainting.

With that in place, we can update the `home-page` component to render the posts:

```clojure
(defn home-page []
  [:div.card>div.card-block
   [display-posts @posts]])
```

### Task 3: Manipulating the data

We're able to load the posts, and have a UI for render them. Let's take a look at adding the ability to sort the posts, and see how the UI will track the changes for us.

 We'll add a `sort-posts` component function that looks as follows:

```clojure
(defn sort-posts [title sort-key]
  (when-not (empty? @posts)
    [:button.btn.btn-secondary
     {:on-click #(swap! posts (partial sort-by sort-key))}
     (str "sort posts by " title)]))
```

This function will check that the posts are not empty, and add a button to sort the posts by the specified key.

Let's add a couple of buttons to the `home-page` that will allow us to sort posts by their score and comments:

```clojure
(defn home-page []
  [:div.card>div.card-block
   [:div.btn-group
    [sort-posts "score" :score]
    [sort-posts "comments" :num_comments]]
   [display-posts @posts]])
```

Note that as we're updating the UI, we're retaining the state of the application. As new components are added, the `posts` atom state is retained. We can modify the way the UI looks without having to reload the application to see the changes.

### Task 4: JavaScript interop

So far we've been working exclusively with Reagent components. Now, let's take a look at using a plain JavaScript library that expects to manipulate the DOM directly.

Let's create a new namespace called `reddit-viewer.chart` in the `src/reddit_viewer/chart.cljs` file to handle charting our data using the Chart.js library. The namespace declaration will look as follows:


```clojure
(ns reddit-viewer.chart
  (:require
    [cljsjs.chartjs]
    [reagent.core :as r]))
```

Next, we'll write a function that calls Chart.js to render given data in a DOM node as a bar chart:

```clojure
(defn render-data [node data]
  (js/Chart.
    node
    (clj->js
      {:type    "bar"
       :data    {:labels   (map :title data)
                 :datasets [{:label "votes"
                             :data  (map :score data)}]}
       :options {:scales {:xAxes [{:display false}]}}})))
```

The above code is equivalent to writing the following JavaScript:

```
new Chart(node
          {type: "bar",
           data: {
                  labels: data.map(function(x) {return x.title}),
                  datasets:
                  [{
                    label: "votes",
                    data: data.map(function(x) {return x.ups})
                   }]
                  },
           options: {
                     scales: {xAxes: [{display: false}]}
                    }
           });
```

Now that we have the code to render the chart, we need to have access to a DOM node. Since Reagent is based on React, it uses a virtual DOM and renders components in the browser DOM as needed.

So far we've been writing components as functions that return HTML elements. However, these functions only represent the render method of a React class.

In order to get access to the DOM we have to implement other lifecycle functions that get called when the component is mounted, updated, and unmounted. This is achieved by calling the `create-class` function:

```clojure
(defn chart-posts-by-votes [data]
  (let [chart (r/atom nil)]
    (r/create-class
      {:component-did-mount  (render-chart chart data)
       :component-did-update (render-chart chart data)
       :component-will-unmount (fn [_] (destroy-chart chart))
       :render               (fn [] (when @data [:canvas]))})))
```

The function accepts a map keyed on the lifecycle events. Whenever each event occurs, the associated function will be called.

We'll track the state of the chart using an atom. This will be necessary because we have to destroy the existing chart when component is unmounted.

You can see that the `:render` key points to a function that will return the `:canvas` element when data is available.

The `:component-did-mount` and `:component-did-update` keys point to the `render-chart` function that w'll write next:

```clojure
(defn render-chart [chart data]
  (fn [component]
    (when (not-empty @data)
      (let [node (r/dom-node component)]
        (destroy-chart chart)
        (reset! chart (render-data node @data))))))
```

This function is a closure that returns a function that will receive the React component. The inner function will check if there's any data available, and if so, then it will grab the mounted DOM node by calling `r/dom-node` on the `component`. It will attempt to clear the existing chart by calling the `destroy-chart` function, and then create a new chart by calling the `render-data` function we wrote earlier.

Finally, we'll implement the `destroy-chart` function as follows:

```clojure
(defn destroy-chart [chart]
  (when @chart
    (.destroy @chart)
    (reset! chart nil)))
```

This function will check whether there's an existing chart present and call its `destroy` method. It will then reset the `chart` atom to a `nil` value.

With that in place, we can navigate back to the `reddit-viewer.core` namespace, and require the `reddit-viewer.chart` namespace there:

```clojure
(ns reddit-viewer.core
  (:require
    [ajax.core :as ajax]
    [reagent.core :as r]
    [reddit-viewer.chart :as chart]))
```

We'll now update the `home-page` component to display the chart:

```clojure
(defn home-page []
  [:div.card>div.card-block
   [:div.btn-group
    [sort-posts "score" :score]
    [sort-posts "comments" :num_comments]]
   [chart/chart-posts-by-votes posts]
   [display-posts @posts]])
```

We should now see the chart rendered, and it should update when we change the sort order of our data using the `score` and `comment` sorting buttons.

### Task 5: Managing local state within components

As a final touch, let's add a navbar to separate the posts and the chart into separate views. We'll start by adding a `navitem` function that creates a navigation link given a title, an atom containing the currently selected view, and the id of the nav item:

```clojure
(defn navitem [title view id]
  [:li.nav-item
   {:class-name (when (= id @view) "active")}
   [:a.nav-link
    {:href     "#"
     :on-click #(reset! view id)}
    title]])
```

The component checks whether the current id in the view matches the item id in order to decide whether its class should be set to active. When it's clicked, the component will reset the `view` atom to its id.

We can now create a Bootstrap navbar with links to posts and the chart:

```clojure
(defn navbar [view]
  [:nav.navbar.navbar-toggleable-md.navbar-light.bg-faded
   [:ul.navbar-nav.mr-auto.nav
    {:className "navbar-nav mr-auto"}
    [navitem "Posts" view :posts]
    [navitem "Chart" view :chart]]])
```

Finally, we'll update the home page to use the `navbar` component. The home page will now need to track a local state to know what view it needs to display.
This is accomplished by creating a local atom called `view`:

```clojure
(defn home-page []
  (let [view (r/atom :posts)]
    (fn []
      [:div
       [navbar view]
       [:div.card>div.card-block
        [:div.btn-group
         [sort-posts "score" :score]
         [sort-posts "comments" :num_comments]]
        (case @view
          :chart [chart/chart-posts-by-votes posts]
          :posts [display-posts @posts])]])))
```

Notice that we return an anonymous function from inside the `let` statement. This is a Reagent mechanic for creating local state within components.

If the inner function was not present, then the top level function would be called each time the component was repainted and the `let` statement would be reinitialized.

When a component returns a function as the result, Reagent knows to call that function when subsequent calls to that component occur.

 Since this is a common operation, Reagent provides a helper macro called `with-let`. We can rewrite the above function using it as follows:

```clojure
(defn home-page []
  (r/with-let [view (r/atom :posts)]
    [:div
     [navbar view]
     [:div.card>div.card-block
      [:div.btn-group
       [sort-posts "score" :score]
       [sort-posts "comments" :num_comments]]
      (case @view
        :chart [chart/chart-posts-by-votes posts]
        :posts [display-posts @posts])]]))
```

That completes all the functionality we set out to add to our application. The only thing left to do is to compile it for production use.

## Compiling for release

So far we've been working with ClojureScript in development mode. This compilation method allows for fast incremental compilation and reloading. However, it generates very large JavaScript files.

To use our app in production we'll want to use the advanced compilation method that will produce optimized JavaScript. This is accomplished by running the following command:

    lein package

This will produce a single minified JavaScript file called `public/js/app.js` that's ready for production use.

## Libraries used in the project

* [Chart.js](http://www.chartjs.org/) - used to generate the bar chart
* [cljs-ajax](https://github.com/JulianBirch/cljs-ajax) - used to fetch data from Reddit
* [Reagent](reagent-project.github.io) - ClojureScript interface for React

