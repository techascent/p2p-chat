(ns p2p-chat.main
  (:require [taoensso.timbre :as log]
            [p2p-chat.server :as server]
            [p2p-chat.routes :as routes])
  (:gen-class))

(defn -main
  [& args]
  (server/restart routes/routes 8000))
