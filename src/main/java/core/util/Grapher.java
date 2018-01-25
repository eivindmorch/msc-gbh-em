package core.util;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import core.model.btree.BehaviorTreeUtil;
import core.training.Chromosome;
import core.model.btree.task.NamedTask;
import core.training.Population;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Grapher {

    private static HashMap<Task, Graph> taskGraphMap = new HashMap<>();

    public static void graph(Task root, String title) {
        Graph graph = new Graph(title + " " + root.toString(), root);
        taskGraphMap.put(root, graph);
    }

    public static void graph(Task root) {
        graph(root, root.toString());
    }

    public static void graph(Chromosome chromosome) {
        Task root = chromosome.getBtree();
        graph(root, chromosome.toString());
    }

    public static <C extends Chromosome> void graph(Population<C> population) {
        for (C chromosome : population.getChromosomes()) {
            Grapher.graph(chromosome.getBtree());
        }
    }

    public static void closeGraph(Task root){
        Graph graph = taskGraphMap.remove(root);
        if (graph != null) {
            graph.close();
        }
    }

    public static <C extends Chromosome> void closeGraphs(Population<C> population){
        for (C chromosome : population.getChromosomes()) {
            Grapher.closeGraph(chromosome.getBtree());
        }
    }

    public static void closeAllGraphs() {
        for (Graph graph : taskGraphMap.values()) {
            graph.close();
        }
        taskGraphMap = new HashMap<>();
    }


    private static class Graph extends JFrame implements Runnable {

        private Task root;
        private ArrayList<Object> vertices;
        private com.mxgraph.view.mxGraph mxGraph;
        private Object graphParent;

        Graph(String title, Task root) {
            super(title);
            this.root = root;

            this.vertices = new ArrayList<>();

            this.mxGraph = new mxGraph();
            this.graphParent = mxGraph.getDefaultParent();

            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.setMinimumSize(new Dimension(1530, 500));

            Thread thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            Task rootClone = BehaviorTreeUtil.clone(root);

            mxGraph.getModel().beginUpdate();
            try {
                Object vertex = addVertex(getTaskName(rootClone));
                graphSubtree(rootClone, vertex);
            } finally {
                mxGraph.getModel().endUpdate();
            }
            resizeVertices();
            fixLayout();
            addToContentPane();
        }

        private void resizeVertices() {
            mxGraph.getModel().beginUpdate();
            try {
                // Resize cells
                for (Object vertex : vertices) {
                    mxCell cell = (mxCell) vertex;
                    mxGeometry g = (mxGeometry) cell.getGeometry().clone();
                    mxRectangle bounds = mxGraph.getView().getState(cell).getLabelBounds();
                    g.setHeight(bounds.getHeight() + 10);
                    g.setWidth(bounds.getWidth() + 20);
                    mxGraph.cellsResized(new Object[]{cell}, new mxRectangle[]{g});
                }
            } finally {
                mxGraph.getModel().endUpdate();
            }
        }

        private void fixLayout() {
            mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(mxGraph);
            treeLayout.setHorizontal(false);
            treeLayout.execute(graphParent);
        }

        private void addToContentPane() {
            mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);
            getContentPane().add(graphComponent);
            this.setVisible(true);
        }

        private void graphSubtree(Task root, Object rootVertex) {

            for (int i = 0; i < root.getChildCount(); i++) {
                Task child = root.getChild(i);
                String childName = getTaskName(child);

                Object childVertex = addVertex(childName);
                addEdge(rootVertex, childVertex);

                graphSubtree(child, childVertex);
            }
        }

        private String getTaskName(Task task) {
            if (task instanceof Selector) {
                return "?";
            } else if (task instanceof Sequence) {
                return "->";
            } else {
                NamedTask namedTask = (NamedTask) task;
                return namedTask.getName();
            }
        }

        private Object addVertex(String name) {
            Object vertex = mxGraph.insertVertex(graphParent, null, name, 0, 0, 0, 0);
            vertices.add(vertex);
            return vertex;
        }

        private Object addEdge(Object parent, Object child) {
            Object edge = mxGraph.insertEdge(graphParent, null, "", parent, child);
            return edge;
        }

        void close() {
            setVisible(false);
            dispose();
        }
    }
}
