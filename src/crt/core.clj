(ns crt.core
   (:import [javax.swing JFrame JTree JScrollPane SwingConstants]
           [javax.swing.tree DefaultMutableTreeNode]
           [com.mxgraph.swing mxGraphComponent]
           [com.mxgraph.view mxGraph]
           [com.mxgraph.layout mxCompactTreeLayout]
           [com.mxgraph.layout mxStackLayout]
           [com.mxgraph.layout mxOrganicLayout]
           [com.mxgraph.layout mxFastOrganicLayout]
           [com.mxgraph.layout.hierarchical mxHierarchicalLayout]
           ))

(defn ns-matches? [ns pattern]
  (re-matches (re-pattern (str pattern ".*")) (str ns)))

(defn interesting-ns? [ns]
  (not (or (ns-matches? ns "clojure")
           (ns-matches? ns "swank")
           (ns-matches? ns "complete.core")
           (ns-matches? ns "user")
           ;;(ns-matches? ns "crt")
           )))

(defn make-roots []
  (filter interesting-ns? (all-ns)))

(defprotocol GraphNode
  (node-name [this])
  (node-edges [this]))

(extend-type clojure.lang.Namespace
  GraphNode
  (node-name [this] (str (ns-name this)))
  (node-edges [this] (map (comp :ns meta second) (ns-refers this))))
;;(concat (ns-publics this) (ns-interns this)))

(extend-type clojure.lang.Var
  GraphNode
  (node-name [this] (:name (meta this)))
  (node-edges [this] [(:ns (meta this))]))

(extend-type clojure.lang.MapEntry
  GraphNode
  (node-name [this] (str (key this)))
  (node-edges [this] [(val this)]))


(defmacro with-update [graph & body]
  `(try
     (.. ~graph (getModel) (beginUpdate))
     ~@body
     (finally
      (.. ~graph (getModel) (endUpdate)))))

(defn- build-node [graph root node parent visited edged]
  (let [visited (or visited (atom {}))
        edged (or edged (atom {}))]
    (if (contains? @visited node)
      (let [v (@visited node)]
        ;;(when (and parent (not (contains? (@edged node))))
        ;;  (.insertEdge graph root nil nil parent v))
        v)
      (let [name (node-name node)
            children (node-edges node)
            v (.insertVertex graph root nil name 0 0 100 25)
            e (when parent (.insertEdge graph root nil nil parent v))
            ]
        (.setConnectable v false)
        (swap! visited #(assoc % node v))
        (doseq [child children]
          (build-node graph root child v visited edged))
        v))))

(defn make-graph []
  (let [graph (mxGraph.)
        root (.getDefaultParent graph)]
    (with-update graph
      (let [visited (atom {})]
        (doseq [node (make-roots)]
          (build-node graph root node nil visited nil))))
    (doto graph
      (.setCellsLocked true)

      ;; (.setCellsEditable false)
      ;; (.setCellsMovable false)
      ;; (.setCellsResizable false)
      ;; (.setCellsResizable false)
      ;; (.setCellsBendable false)
      ;; (.setCellsCloneable false)
      ;; (.setCellsDisconnectable false)
      ;; (.setConnectableEdges false)
      ;; (.setAutoSizeCells true)
      ;; (.setAutoOrigin true)
      )
    ;;(mxCompactTreeLayout. graph)
    ;;(mxStackLayout. graph)
    ;;(.execute (mxOrganicLayout. graph) root)
    ;;(.execute (mxFastOrganicLayout. graph) root-node)
    (let [;;layout (mxCompactTreeLayout. graph)
          layout (mxHierarchicalLayout. graph)
          ]
      (doto layout
        ;;(.setLevelDistance 100)
        (.setIntraCellSpacing 5)
        (.setInterRankCellSpacing 100)
        (.setOrientation SwingConstants/WEST)
        (.execute root)))
    (let [component (mxGraphComponent. graph)]
      component)))

(defn open-crt []
  (let [frame (JFrame. "crt")]
    (.add (.getContentPane frame)
          (make-graph)
          )
    ;;(JScrollPane. (make-tree)))
    (doto frame
      (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
      (.setSize 600 400)
      (.setVisible true)
      )))