(ns p2p-chat.webapp
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [p2p-chat.events :as events]))

(def topic "/news")

(defn create-node
  [err node]
  (when err
    (js/console.log "Could not create the Node, check if your browser has WebRTC Support")
    (throw (js/Error. "Node creation.")))
  (.on node "peer:discovery"  (fn [peer-info] (rf/dispatch [:peer/discovery peer-info])))
  (.on node "peer:connect"    (fn [peer-info] (rf/dispatch [:peer/connect peer-info])))
  (.on node "peer:disconnect" (fn [peer-info] (rf/dispatch [:peer/disconnect peer-info])))
  (rf/dispatch [:node/start node]))

(defn connect
  []
  (if window.createNode
    (window.createNode create-node)
    (js/setTimeout connect 1000)))

(defn node-subscribe [topic] (rf/dispatch [:node/subscribe topic]))
(defn node-send [topic text] (rf/dispatch [:chat/send topic text]))

(defn chat-view
  [handle*]
  (let [messages* (rf/subscribe [:chat/messages "/news"])
        text* (r/atom "")
        send-text (fn []
                    (node-send topic {:handle @handle* :text @text*})
                    (reset! text* ""))]
  (fn []
    [:div.chat
     "Messages: "
     (into [:div.messages]
           (for [{:keys [handle text] :as msg} @messages*]
             [:div.message [:b handle ": "] text]))
     [:input.send {:value @text*
              :on-key-down #(when (= "Enter" (.-key %))
                              (.preventDefault %)
                              (send-text))
              :on-change #(reset! text* (-> % .-target .-value))}]
     [:button.send {:on-click (fn [e] (send-text))} "Send"]])))

(defn app
  []
  (let [connected-peers* (rf/subscribe [:peers/connected])
        handle* (r/atom "")]
    (fn  []
      [:div
       [:h3 "P2P Chat"]
       [:div.top-bar
        [:button {:on-click (fn [e] (node-subscribe "/news"))} "Subscribe"]]
       [:b "Handle: "]
       [:input.handle {:value @handle*
                :on-change #(reset! handle* (-> % .-target .-value))}]
       [chat-view handle*]])))

(events/register!)

(connect)
(r/render [app] (js/document.getElementById "app"))
