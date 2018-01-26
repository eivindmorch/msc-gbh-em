package core.util.Graphing;

import com.badlogic.gdx.ai.btree.Task;
import core.training.Chromosome;
import core.training.Population;
import java.util.HashMap;


public abstract class Grapher {

    private static HashMap<Task, GraphDisplay> taskGraphMap = new HashMap<>();
    private static HashMap<Population, GraphDisplay> populationGraphMap = new HashMap<>();

    public static void graph(String title, Task root) {
        taskGraphMap.put(root, new GraphDisplay(title, root));
    }

    public static void graph(Task root) {
        graph(root.toString(), root);
    }

    public static void graph(Chromosome chromosome) {
        graph(chromosome.toString(), chromosome.getBtree());
    }

    public static <C extends Chromosome> void graph(String title, Population<C> population) {
        populationGraphMap.put(population, new GraphDisplay(title, population));
    }

    public static <C extends Chromosome> void graph(Population<C> population) {
        graph("Population@" + population.hashCode(), population);
    }


    public static void closeGraph(Task root){
        GraphDisplay graphDisplay = taskGraphMap.remove(root);
        if (graphDisplay != null) {
            graphDisplay.close();
        }
    }

    public static <C extends Chromosome> void closeGraphs(Population<C> population){
        GraphDisplay graphDisplay = populationGraphMap.get(population);
        if (graphDisplay != null) {
            graphDisplay.close();
        }
    }

    public static void closeAllSingleGraphs() {
        for (GraphDisplay graphDisplay : taskGraphMap.values()) {
            graphDisplay.close();
        }
        taskGraphMap = new HashMap<>();
    }

    public static void closeAllPopulationGraphs() {
        for (GraphDisplay graphDisplay : populationGraphMap.values()) {
            graphDisplay.close();
        }
        populationGraphMap = new HashMap<>();
    }

    public static void closeAllGraphs() {
        closeAllSingleGraphs();
        closeAllPopulationGraphs();
    }


//    private static class Graph extends JFrame implements Runnable {
//
//        private ArrayList<Task> roots;
//
//        Graph(String title, Task root) {
//            super(title);
//            this.roots = new ArrayList<>();
//            this.roots.add(root);
//            init();
//        }
//
//        Graph(String title, Population<?> population) {
//            super(title);
//            this.roots = new ArrayList<>();
//            for (Chromosome chromosome : population.getChromosomes()) {
//                this.roots.add(chromosome.getBtree());
//            }
//            init();
//        }
//
//        private void init() {
//            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//            this.setMinimumSize(new Dimension(1530, 500));
//
//            this.setLayout(new BorderLayout());
//
//            Thread thread = new Thread(this);
//            thread.start();
//        }
//
//        @Override
//        public void run() {
//            for (Task root : roots) {
//                Task rootClone = BehaviorTreeUtil.clone(root);
//
//                mxGraph mxGraph = new mxGraph();
//                Object graphParent = mxGraph.getDefaultParent();
//                ArrayList<Object> vertices = new ArrayList<>();
//
//                mxGraph.getModel().beginUpdate();
//                try {
//                    Object vertex = addVertex(getTaskName(rootClone), mxGraph, graphParent, vertices);
//                    graphSubtree(rootClone, vertex, mxGraph, graphParent, vertices);
//                } finally {
//                    mxGraph.getModel().endUpdate();
//                }
//                resizeVertices(mxGraph, vertices);
//                fixLayout(mxGraph, graphParent);
//                addToContentPane(mxGraph);
//            }
//            this.setVisible(true);
//
//        }
//
//        private void resizeVertices(mxGraph mxGraph, ArrayList<Object> vertices) {
//            mxGraph.getModel().beginUpdate();
//            try {
//                // Resize cells
//                for (Object vertex : vertices) {
//                    mxCell cell = (mxCell) vertex;
//                    mxGeometry g = (mxGeometry) cell.getGeometry().clone();
//                    mxRectangle bounds = mxGraph.getView().getState(cell).getLabelBounds();
//                    g.setHeight(bounds.getHeight() + 10);
//                    g.setWidth(bounds.getWidth() + 20);
//                    mxGraph.cellsResized(new Object[]{cell}, new mxRectangle[]{g});
//                }
//            } finally {
//                mxGraph.getModel().endUpdate();
//            }
//        }
//
//        private void fixLayout(mxGraph mxGraph, Object graphParent) {
//            mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(mxGraph);
//            treeLayout.setHorizontal(false);
//            treeLayout.execute(graphParent);
//        }
//
//        private void addToContentPane(mxGraph mxGraph) {
//            mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);
//            getContentPane().add(graphComponent, BorderLayout.AFTER_LAST_LINE);
//        }
//
//        private void graphSubtree(Task root, Object rootVertex, mxGraph mxGraph, Object graphParent, ArrayList<Object> vertices) {
//
//            for (int i = 0; i < root.getChildCount(); i++) {
//                Task child = root.getChild(i);
//                String childName = getTaskName(child);
//
//                Object childVertex = addVertex(childName, mxGraph, graphParent, vertices);
//                addEdge(rootVertex, childVertex, mxGraph, graphParent);
//
//                graphSubtree(child, childVertex, mxGraph, graphParent, vertices);
//            }
//        }
//
//        private String getTaskName(Task task) {
//            if (task instanceof Selector) {
//                return "?";
//            } else if (task instanceof Sequence) {
//                return "->";
//            } else {
//                NamedTask namedTask = (NamedTask) task;
//                return namedTask.getName();
//            }
//        }
//
//        private Object addVertex(String name, mxGraph mxGraph, Object graphParent, ArrayList<Object> vertices) {
//            Object vertex = mxGraph.insertVertex(graphParent, null, name, 0, 0, 0, 0);
//            vertices.add(vertex);
//            return vertex;
//        }
//
//        private Object addEdge(Object parent, Object child, mxGraph mxGraph, Object graphParent) {
//            Object edge = mxGraph.insertEdge(graphParent, null, "", parent, child);
//            return edge;
//        }
//
//        void close() {
//            setVisible(false);
//            dispose();
//        }
//    }

}
