(ns test.core
  (:require-macros [cljs-react-material-ui.core :refer [adapt-rum-class]])
  (:require [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.rum :as ui]
            [rum.core :as rum]))


;;
;; Globals
;;

;; https://github.com/roylee0704/react-flexbox-grid
(def ui-flexbox-grid (adapt-rum-class js/ReactFlexboxGrid.Grid))
(def ui-flexbox-row (adapt-rum-class js/ReactFlexboxGrid.Row))
(def ui-flexbox-col (adapt-rum-class js/ReactFlexboxGrid.Col))

;;
;; Setup
;;

(def ui-values (atom {:val 500}))

(rum/defc mycomp < rum/reactive []
  (ui/mui-theme-provider
   {:mui-theme (get-mui-theme {:palette {:text-color (color :blue900)}})}
   [:div [:p (:val (rum/react ui-values))] ; <-- actually now fails even with this removed
    (ui/slider {:min 200 :max 20000
                :value (:val (rum/react ui-values))
                :on-change #(swap! ui-values assoc :val %2)})]))

(rum/defc app
  < rum/reactive
  []
  [:div {:style {:position "absolute"
                 :max-width "700px"
                 :margin "auto" :top "5rem" :bottom "0" :left "0" :right "0"}}
   (ui/mui-theme-provider
    {:mui-theme (get-mui-theme {:palette {:text-color (color :grey900)}})}
    [:div
     [:h1.title.center "TEST"]]
    (mycomp))])

(rum/mount (app) (js/document.getElementById "app"))
