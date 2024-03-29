(defproject {{ns-name}} "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript  "1.10.238" {{#via?}}:exclusions [com.cognitect/transit-clj]{{/via?}}]

                 {{#reflecti?}}[com.7theta/reflecti "1.7.2"]{{/reflecti?}}
                 {{^reflecti?}}
                 [reagent "0.8.1"
                  :exclusions [cljsjs/react
                               cljsjs/react-dom
                               cljsjs/react-dom-server]]
                 [cljsjs/react-dom "16.4.1-0"]
                 [cljsjs/react "16.4.1-0"]
                 {{/reflecti?}}
                 [re-frame "0.10.5"]
                 {{#garden?}}[garden "1.3.5"]{{/garden?}}
                 {{#server?}}
                 [http-kit "2.3.0"]
                 [ring/ring-core "1.6.3"{{#via?}} :exclusions [commons-codec]{{/via?}}]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-anti-forgery "1.2.0"]
                 [compojure "1.6.0"]
                 {{#via?}}[com.7theta/via "1.0.1"]{{/via?}}

                 [integrant "0.6.3"]
                 [yogthos/config "1.1.1"]
                 {{/server?}}]
  :clean-targets ^{:protect false} ["target" "resources/public/js/compiled"{{#test?}}
                                    "test/js"{{/test?}}{{#garden?}}
                                    "resources/public/css"{{/garden?}}]
  :profiles {:dev {:source-paths ["dev/clj" {{#server?}}"src/clj"{{/server?}}]
                   :dependencies [[binaryage/devtools "0.9.9"]
                                  [figwheel-sidecar "0.5.15"]
                                  [com.cemerick/piggieback "0.2.2"]{{#server?}}
                                  [integrant/repl "0.3.1"]{{/server?}}{{#trace?}}
                                  [day8.re-frame/re-frame-10x "0.3.0-2-react16"]{{/trace?}}]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :plugins [[lein-cljsbuild "1.1.7" :exclusions [org.apache.commons/commons-compress]]{{#garden?}}
                             [lein-garden "0.3.0" :exclusions [org.clojure/clojure]]{{/garden?}}{{#less?}}
                             [lein-less "1.7.5"]{{/less?}}
                             [lein-figwheel "0.5.15" :exclusions [org.clojure/clojure]]{{#test?}}
                             [lein-doo "0.1.7"]{{/test?}}]}{{#server?}}
             :uberjar {:source-paths ["prod/clj" {{#server?}}"src/clj"{{/server?}}]
                       :main {{name}}.server
                       :aot :all
                       :prep-tasks [["cljsbuild" "once" "min"]]
                       :uberjar-name "{{name}}.jar"}{{/server?}}}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs" "dev/cljs"]
                        :figwheel {:on-jsload "{{name}}.core/mount-root"}
                        :compiler {:main {{name}}.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true
                                   :preloads [devtools.preload{{#trace?}}
                                              day8.re-frame-10x.preload{{/trace?}}]{{#trace?}}
                                   :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}{{/trace?}}
                                   :external-config {:devtools/config {:features-to-install :all}}}}
                       {:id "min"
                        :source-paths ["src/cljs" "prod/cljs"]
                        :jar true
                        :compiler {:main {{name}}.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :pretty-print false}}
                       {:id "test"
                        :source-paths ["src/cljs" "dev/cljs" "test/cljs"]
                        :compiler {:main {{name}}.runner
                                   :output-to "resources/public/js/compiled/test.js"
                                   :output-dir "resources/public/js/compiled/test/out"
                                   :optimizations :none}}]}
  {{#garden?}}:garden {:builds [{:id "screen"
                                 :source-paths ["src/clj"]
                                 :stylesheet {{name}}.css/screen
                                 :compiler {:output-to "resources/public/css/screen.css"
                                            :pretty-print? true}}]}{{/garden?}}{{#less?}}
  :less {:source-paths ["less"]
         :target-path "resources/public/css"}
  {{/less?}}:prep-tasks [{{#garden?}} ["garden" "once"] {{/garden?}}{{#less?}}
                         ["less" "once"]{{/less?}} "compile"])
