(ns p2p-chat.css.site
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px percent em]]))

(defstyles site
  [[:div.messages
    {:padding-top (px 4)
     :border "1px solid #CCC"
     :margin-bottom (px 10)
     :border-radius (px 4)
     :width (px 600)
     :height (px 300)}
    [:div.message
     {:padding-top (px 3)
      :padding-left (px 10)
      :padding-bottom (px 3)}]]
   [:div.top-bar
    {:margin-bottom (px 10)}
    [:button {:margin-right (px 5)}]]
   [:input
    {:height (px 25)
     :border-radius (px 4)
     :border "1px solid #CCC"}]
   [:input.send
    {:height (px 25)
     :border-radius (px 4)
     :border "1px solid #CCC"
     :width (px 515)
     :margin-right (px 10)}]
   [:button
    {:background :#EEE
     :height (px 27)
     :border-radius (px 4)
     :border "1px solid #CCC"}]
   [:button.send
    {:width (px 75)}]])
