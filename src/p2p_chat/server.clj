(ns p2p-chat.server
  (:require [taoensso.timbre :as log]
            [org.httpkit.server :as http]
            [muuntaja.middleware :refer [wrap-format]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.params :refer [wrap-params]]
            [bidi.ring :as bidi-ring]))

(defonce server-stop-fn* (atom nil))

(defn handler
  [routes]
  (-> (bidi-ring/make-handler routes)
      (wrap-format)
      (wrap-params)
      (wrap-resource "public")
      (wrap-content-type)))

(defn stop
  []
  (when @server-stop-fn*
    (@server-stop-fn*)))

(defn restart
  [routes port]
  (stop)
  (->> (http/run-server (handler routes) {:port port})
         (reset! server-stop-fn*))
  (println "Server started on port:" port))

(defn start
  []
  (restart))
