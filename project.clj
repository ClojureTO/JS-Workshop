(defproject reddit-viewer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.10.0" :scope "provided"]
                 [org.clojure/clojurescript "1.10.520" :scope "provided"]
                 [reagent "0.8.1"]
                 [cljsjs/chartjs "2.7.3-0"]
                 [cljs-ajax "0.8.0"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.18"]]

  :min-lein-version "2.5.0"
  :source-paths ["src"]
  :clean-targets ^{:protect false}
[:target-path
 [:cljsbuild :builds :app :compiler :output-dir]
 [:cljsbuild :builds :app :compiler :output-to]]

  :resource-paths ["public"]

  :figwheel {:http-server-root "."
             :nrepl-port       7002
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :css-dirs         ["public/css"]}

  :cljsbuild {:builds {:app
                       {:source-paths ["src" "env/dev/cljs"]
                        :compiler
                                      {:main          "reddit-viewer.dev"
                                       :output-to     "public/js/app.js"
                                       :output-dir    "public/js/out"
                                       :asset-path    "js/out"
                                       :source-map    true
                                       :optimizations :none
                                       :pretty-print  true}
                        :figwheel
                                      {:open-urls ["http://localhost:3449/index.html"]
                                       :on-jsload "reddit-viewer.core/mount-root"}}
                       :release
                       {:source-paths ["src" "env/prod/cljs"]
                        :compiler
                                      {:output-to     "public/js/app.js"
                                       :output-dir    "public/js/release"
                                       :asset-path    "js/out"
                                       :optimizations :advanced
                                       :pretty-print  false}}}}

  :aliases {"package" ["do" "clean" ["cljsbuild" "once" "release"]]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [figwheel-sidecar "0.5.18"]
                                  [nrepl "0.6.0"]
                                  [cider/piggieback "0.4.0"]]}})
