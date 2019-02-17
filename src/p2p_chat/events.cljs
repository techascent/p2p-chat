(ns p2p-chat.events
  (:require [re-frame.core :as rf]
            [cognitect.transit :as transit]))

(defonce registered?* (atom false))

(defn peer-info->id
  [peer-info]
  (-> peer-info .-id .toB58String))

(defn subscription-data
  [msg]
  (let [msg (js->clj msg)
        reader (transit/reader :json)
        data (.read reader (js/JSON.parse (.toString (get msg "data"))))
        channel (-> msg (get "topicIDs") first)]
    (rf/dispatch [:chat/add-data channel data])))

(defn register-peer-management
  []
  (rf/reg-event-db :peer/dial-failure
                   [rf/trim-v]
                   (fn [db [peer-info]]
                     (update-in db [:peers/dialing] dissoc (peer-info->id peer-info))))

  (rf/reg-event-db :peer/discovery
                   [rf/trim-v]
                   (fn [db [peer-info]]
                     (let [id (peer-info->id peer-info)
                           node (:node db)]
                       (if (and (not (get-in db [:peers/dialing id]))
                                (not (get-in db [:peers/connected id])))
                         (do
                           (.dial node peer-info
                                  (fn [err conn]
                                    (let [time-to-next-dial (+ (* 25 1000) (* (Math/random 0) 10000))]
                                      (js/setTimeout #(rf/dispatch [:peer/dial-failure peer-info]) time-to-next-dial))))
                           (update-in db [:peers/dialing id] conj peer-info))
                         db))))

  (rf/reg-event-db :peer/connect
                   [rf/trim-v]
                   (fn [db [peer-info]]
                     (let [id (peer-info->id peer-info)]
                       (assoc-in db [:peers/connected id] peer-info))))

  (rf/reg-event-db :peer/disconnect
                   [rf/trim-v]
                   (fn [db [peer-info]]
                     (let [id (peer-info->id peer-info)]
                       (-> db
                           (update-in [:peers/dialing] dissoc id)
                           (update-in [:peers/connected] dissoc id)))))

  (rf/reg-event-db :node/start
                   [rf/trim-v]
                   (fn [db [node]]
                     (.start node
                             (fn [err]
                               (when err
                                 (js/console.log "WebRTC not supported.")
                                 (throw (js/Error. "Start.")))))
                     (-> db
                         (assoc :node node)
                         (assoc :id (-> node .-peerInfo .-id .toB58String)))))

  (rf/reg-event-fx :chat/subscribe
                   [rf/trim-v]
                   (fn [cofx [channel]]
                     (let [db (:db cofx)
                           node (:node db)]
                       (if-not ((set (:chat/channels db)) channel)
                         (-> node .-pubsub (.subscribe channel
                                                       subscription-data
                                                       (fn [err] (if err (println "Error subscribing: " err))))))
                       {:db (-> db
                                (update :chat/channels (comp vec concat) [channel]))})))

  (rf/reg-event-fx :chat/send
                   [rf/trim-v]
                   (fn [cofx [channel data]]
                     (let [node (-> cofx :db :node)
                           writer (transit/writer :json)
                           buffer-data (->> data
                                            (.write writer)
                                            js/JSON.stringify
                                            js/window.Buffer.from)
                           err-callback (fn [err] (if err (println "Error: " err)))]
                       (-> node .-pubsub (.publish channel buffer-data err-callback))
                       {})))

  (rf/reg-event-db :chat/add-data
                   [rf/trim-v]
                   (fn [db [channel message]]
                     (update-in db [:chat/messages channel] concat [message])))

  (rf/reg-sub :chat/messages
              (fn [db [_ channel]]
                (if channel
                  (get-in db [:chat/messages channel])
                  [])))

  (doseq [k [:peers/dialing :peers/connected :chat/channels]]
    (rf/reg-sub k (fn [db _] (get db k)))))

(defn register!
  []
  (when-not @registered?*
    (reset! registered?* true)
    (register-peer-management)

    (rf/reg-sub :current (fn [db path]
                           (get-in db path)))))
