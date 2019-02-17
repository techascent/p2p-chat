(ns p2p-chat.webapp
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [p2p-chat.events :as events]))

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

(defn chat-subscribe [channel] (rf/dispatch [:chat/subscribe channel]))

(defn send-text
  [handle* current-channel* text*]
  (rf/dispatch [:chat/send @current-channel* {:handle @handle* :text @text*}])
  (reset! text* ""))

(defn chat-send-view
  [handle* current-channel*]
  (let [text* (r/atom "")]
    (fn [handle* current-channel*]
      [:div
       [:input.send {:value @text*
                     :on-key-down #(when (= "Enter" (.-key %))
                                     (.preventDefault %)
                                     (send-text handle* current-channel* text*))
                     :on-change #(reset! text* (-> % .-target .-value))}]
       [:button.send {:on-click (fn [e] (send-text handle* current-channel* text*))} "Send"]])))

(defn chat-view
  [handle* current-channel*]
  (let [channels* (rf/subscribe [:chat/channels])]
    (fn [handle* current-channel*]
      (let [messages* (rf/subscribe [:chat/messages @current-channel*])]
        [:div.chat
         (into [:div.channels]
               (for [t @channels*]
                 [:div.channel (merge {:on-click #(reset! current-channel* t)}
                                      (if (= @current-channel* t)
                                        {:class "selected"})) t]))
         (into [:div.messages]
               (for [{:keys [handle text] :as msg} @messages*]
                 [:div.message [:b handle ": "] text]))
         [chat-send-view handle* current-channel*]]))))

(defn app
  []
  (let [connected-peers* (rf/subscribe [:peers/connected])
        handle* (r/atom "")
        channel* (r/atom "")
        current-channel* (r/atom nil)]
    (fn  []
      [:div
       [:h3 "P2P Chat"]
       [:div.top-bar
        [:b "Channel: "]
        [:input {:value @channel*
                 :on-change #(reset! channel* (-> % .-target .-value))}]
        [:button {:on-click (fn [e]
                              (chat-subscribe @channel*)
                              (reset! current-channel* @channel*))} "Subscribe"]]
       [:div
        [:b "Handle: "]
        [:input {:value @handle*
                 :on-change #(reset! handle* (-> % .-target .-value))}]]
       [chat-view handle* current-channel*]])))

(events/register!)

(connect)
(r/render [app] (js/document.getElementById "app"))
