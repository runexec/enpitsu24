(ns enpitsu24.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]))

(def svg-path "svg/")

(defonce app-state
  (atom
   {:sentence-input  "こんにちは世界! Type Something Here."}))

(defn utf-8->svg-file
  [character] 
  (let [code (->> character
                  js/escape
                  js/encodeURIComponent
                  (re-find #"^%25u(.*)")
                  last)
        code (if code
               code
               (-> character
                   (.charCodeAt 0)
                   (.toString 16)))
        pn (let [x (- 5 (count code))]
             (if (neg? x) 0 x))
        pad-str (reduce str (repeat pn 0))
        padded-code (str pad-str code)]
    (-> padded-code
        clojure.string/lower-case
        (str ".svg"))))

(defn char->svg-path [c parent-dir]
  (let [c (utf-8->svg-file c)
        path (str parent-dir "/" c)]
    (clojure.string/replace path
                            #"//"
                            "/")))

(defn svg-image [character]
  (char->svg-path character svg-path))

(defn str->images [s]
  (map svg-image s))

(defn screenshot! [& _]
  (js/html2canvas (.-body js/document)
                  #js {:onrendered
                       (fn [canvas]
                         (.open js/window
                                (.toDataURL canvas "image/png")))}))

(defn sentence-input! [app owner evt]
  (let [v (->> (.. evt -target -value)
               (take 50)
               (reduce str))]
    (om/update! app [:sentence-input] v)))

(defcomponent sentence-input-view [app owner]
  (render
   [_]
   (dom/input
    {:id "sentence-input"
     :on-change (partial sentence-input! app owner)
     :value (:sentence-input app)
     :maxLength 50})))

(defcomponent sentence-image-view [{:keys [image alt]} owner]
  (render
   [_]
   (dom/img {:src image
             :alt alt
             :style {:width "200px" :height "200px"}})))

(defcomponent sentence-images-view [app owner]
  (render
   [_]
   (let [sentence (:sentence-input app)
         image-paths (str->images sentence)
         images (map (fn [i a]
                       {:image i :alt a})
                     image-paths
                     sentence)]                     
     (dom/div {:id "sentence-images"}
      (om/build-all sentence-image-view images)))))

(defcomponent credits-view [_ _]
  (render
   [_]
   (dom/div
    {:id "links-view"}
    (dom/a {:href "//github.com/runexec/enpitsu24"}
           "Github")
    (dom/a {:href "//github.com/runexec/enpitsu24/blob/master/licenses"}
           "Licenses")
        (dom/a {:href "javascript:void(0);"
            :on-click screenshot!}
           "Save as Image"))))

(defcomponent main-view [app owner]
  (render
   [_]
   (dom/div
    (dom/h1 {:id "logo"} "E n p i t s u 24")
    (om/build sentence-input-view app)
    (om/build sentence-images-view app)
    (om/build credits-view app))))

(defn main []
  (om/root
   main-view
   app-state
   {:target (. js/document (getElementById "app"))}))
