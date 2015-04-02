(ns enpitsu24.kanji-tools)

(def svg-dir "resources/public/svg/")

(defn svg-files
  ([] (svg-files svg-dir))
  ([fp]
   (->> fp
        clojure.java.io/file
        file-seq 
        (remove (memfn isDirectory))
        (filter #(->> % str (re-find #".svg$"))))))

(defn modify-svg! [read-svg-fp]
  (let [r clojure.string/replace
        svg-data (-> (slurp read-svg-fp)
                     (r #"font-size:8;fill:#808080" "font-size:8;fill:#bf0000")
                     (r #"1 0 0 1 " "0.5 0.15 0.1 0.5 ")
                     (r #"stroke-width:3" "stroke-width:1")
                     (r #"width=\"109\" height=\"109\""
                        "width=\"180\" height=\"180\""))]
    (spit read-svg-fp svg-data)))


(defn modify-all!!! []
  (doseq [fp (svg-files)]
    (println "Reading " fp)
    (modify-svg! fp)
    (println "Wrote " fp)))
