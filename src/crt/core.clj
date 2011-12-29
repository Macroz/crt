(ns crt.core
  (:import [javax.swing JFrame JTree]))

(defn make-tree []
  (JTree.))


(defn run []
  (let [frame (JFrame. "crt")]
    (.add (.getContentPane frame) (make-tree))
    (doto frame
      (.setSize 600 400)
      (.setVisible true)
      )))