(ns p2p-chat.css.site
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px percent em]]))

(defstyles site
  [[:div.chat
    [:div.messages
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
    [:div.channels
     {:margin-top (px 5)}
     [:div.channel
      {:color :#444
       :cursor :pointer
       :display :inline-block
       :padding-left (px 8)
       :padding-right (px 8)
       :padding-top (px 3)
       :border-top "1px solid #CCC"
       :border-right "1px solid #CCC"
       :border-left "1px solid #CCC"
       :border-top-right-radius (px 3)
       :border-top-left-radius (px 3)}]
     [:.channel.selected
      {:color :black
       :background :white
       :box-shadow "inset 0px 1px 0px 0px #ccc"
       :cursor :pointer}]]]
   [:div.top-bar
     {:margin-bottom (px 10)}
     [:button {:margin-right (px 5)
               :margin-left (px 5)}]]
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
