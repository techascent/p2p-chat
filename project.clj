(defproject techascent/p2p-chat "0.1.0-SNAPSHOT"
  :description ""
  :url ""

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.490"]
                 [com.taoensso/timbre "4.8.0"]

                 [http-kit "2.3.0"]
                 [ring "1.7.0"]
                 [garden "1.3.6"]
                 [hiccup "1.0.5"]
                 [bidi "2.1.4"]
                 [com.cognitect/transit-cljs "0.8.256"]

                 [metosin/muuntaja "0.6.1"]
                 [org.clojure/clojurescript "1.10.339"]
                 [reagent "0.8.1"]
                 [commonmark-hiccup "0.1.0"]
                 [re-frame "0.10.6"]]

  :plugins [[lein-asset-minifier "0.2.7" :exclusions [org.clojure/clojure]]
            [lein-cljsbuild      "1.1.7"]
            [lein-garden         "0.3.0"]]

  :min-lein-version "2.7.1"
  :uberjar-name     "p2p-chat.jar"
  :main             p2p-chat.main
  :source-paths     ["src"]
  :resource-paths   ["resources" "target/cljsbuild"]

  :garden {:builds [{:id "site"
                     :source-paths ["src"]
                     :stylesheet p2p-chat.css.site/site
                     :compiler {:output-to "resources/public/css/app.css"
                                :pretty-print? true}}]}

  :profiles {:tools {:dependencies [[com.fasterxml.jackson.core/jackson-core "2.6.6"]
                                    [cheshire "5.7.0"]
                                    [http-kit "2.2.0"]]}
             :dev {:dependencies [[re-frisk "0.5.3"]]
                   :source-paths ["env/dev/server"]
                   :plugins [[lein-figwheel "0.5.17"]]

                   :env {:log-system "pretty"
                         :dev        true}}

             :uberjar {:source-paths ["env/prod/server"]
                       :prep-tasks ["compile"
                                    ["garden" "once"]
                                    ["cljsbuild" "once" "min"]
                                    "minify-assets"]
                       :env {}
                       :aot :all
                       :omit-source true}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/"]
                        :figwheel true
                        :compiler {:main "p2p-chat.webapp"
                                   :asset-path "/js/out"
                                   :output-to "resources/public/js/app.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :preloads [re-frisk.preload]}}
                       {:id "prod"
                        :source-paths ["src/"]
                        :compiler {:output-to "resources/public/js/app.js"
                                   :externs ["externs/bundle.js"]
                                   :optimizations :advanced}}]}

  :figwheel {:http-server-root "public"
             :server-port 3449
             :nrepl-port 7002
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :css-dirs ["resources/public/css"]}

  :minify-assets {:assets
                  {"resources/public/css/app.min.css" "resources/public/css/app.css"}}

  :clean-targets ^{:protect false}
  [:target-path
   [:garden :builds :dev :compiler :output-to]
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]])
