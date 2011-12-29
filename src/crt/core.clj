(ns crt.core
  (:import [javax.swing JFrame JTree JScrollPane]
           [javax.swing.tree DefaultMutableTreeNode]))

(defn to-node [element]
  (cond
   (string? element) (DefaultMutableTreeNode. element)
   (or (seq? element)
       (vector? element)) (let [node (DefaultMutableTreeNode. (first element))]
                            (doall (for [child (map to-node (second element))]
                                     (.add node child)))
                            node)
       :else (DefaultMutableTreeNode. "FAIL")))

(defn ns-to-seqs [ns]
  [(str (ns-name ns))
   (map str (keys (ns-publics ns)))])

(defn make-root []
  ["Root" (map ns-to-seqs (all-ns))])

(defn make-tree []
  (JTree. (to-node (make-root))))

(defn open-crt []
  (let [frame (JFrame. "crt")]
    (.add (.getContentPane frame)
          (JScrollPane. (make-tree)))
    (doto frame
      (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
      (.setSize 600 400)
      (.setVisible true)
      )))