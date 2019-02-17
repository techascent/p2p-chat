(ns p2p-chat.routes
  (:require [ring.util.response :as response]
            [hiccup.page :as hiccup]))

(defn- home-page
  [request]
  (let [session-data (-> request :session/data)]
    (-> (hiccup/html5
          [:head
           [:meta {:charset "utf-8"}]
           [:title "p2p-chat"]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
           [:link {:rel "stylesheet" :href "/css/app.css"}]
           [:body
            [:div#app]]
           [:script {:src "/js/app.js" :type "text/javascript"}]
           [:script {:src "/js/bundle.js" :type "text/javascript"}]])
        (response/response)
        (response/header "Content-Type" "text/html"))))


(defn not-found
  [request]
  (response/not-found "Not found."))

(def routes ["/" {:get [[true #'home-page]]}])
