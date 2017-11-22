(ns dev
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require [{{ns-name}}.config :refer [config]]
            [{{ns-name}}.handler :refer [handler]]

            [integrant.core :as ig]
            [com.stuartsierra.component :as component] ; Figwheel dependency

            [integrant.repl :refer [clear go halt init reset reset-all]]
            [integrant.repl.state :refer [system]]

            [figwheel-sidecar.system :as fig]

            [clojure.tools.namespace.repl :refer [refresh refresh-all disable-reload!]]
            [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.test :refer [run-tests run-all-tests]]
            [clojure.pprint :refer [pprint]]
            [clojure.reflect :refer [reflect]]))

(disable-reload! (find-ns 'integrant.core))

{{#via?}}
(def dev-config (assoc config :figwheel {:client-proxy
                                         (ig/ref :via.server.client-proxy/client-proxy)}))
{{/via?}}
{{^via?}}
(def dev-config (assoc config :figwheel nil))
{{/via?}}

(defmethod ig/init-key :figwheel [_ {:keys [client-proxy]}]
  (component/start
   (component/system-map
    :figwheel-system (-> (fig/fetch-config)
                         (assoc-in [:data :figwheel-options :ring-handler]
                                   (handler{{#via?}} client-proxy{{/via?}}))
                         fig/figwheel-system)
    :css-watcher (fig/css-watcher
                  {:watch-paths ["resources/public/css"]}))))

(defmethod ig/halt-key! :figwheel [_ figwheel-system]
  (component/stop figwheel-system))

(ig/load-namespaces dev-config)

(integrant.repl/set-prep! (constantly dev-config))

(defn cljs-repl
  "Initializes and starts the cljs REPL"
  []
  (fig/cljs-repl (get-in system [:figwheel :figwheel-system])))
