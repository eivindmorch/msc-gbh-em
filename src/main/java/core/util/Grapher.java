package core.util;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import core.model.btree.task.NamedTask;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grapher extends JFrame{

    private ArrayList<Object> vertices;
    private mxGraph graph;
    private Object graphParent;

    public Grapher(String title) {
        super(title);

        vertices = new ArrayList<>();

        graph = new mxGraph();
        graphParent = graph.getDefaultParent();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1000, 500));

    }

    private void resizeVertices() {
        graph.getModel().beginUpdate();
        try {
            // Resize cells
            for (Object vertice : vertices) {
                mxCell cell = (mxCell) vertice;
                mxGeometry g = (mxGeometry) cell.getGeometry().clone();
                mxRectangle bounds = graph.getView().getState(cell).getLabelBounds();
                g.setHeight(bounds.getHeight() + 10);
                g.setWidth (bounds.getWidth() + 20);
                graph.cellsResized(new Object[] { cell }, new mxRectangle[] { g });
            }
        } finally {
            graph.getModel().endUpdate();
        }
    }

    private void fixLayout() {
        mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(graph);
        treeLayout.setHorizontal(false);
        treeLayout.execute(graphParent);
    }

    private void addToContentPane() {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
        this.setVisible(true);
    }

    public void graph(BehaviorTree btree) {
        graph.getModel().beginUpdate();
        try {
            Task root = btree.getChild(0);
            Object vertex = addVertex(getTaskName(root));
            graphSubtree(root, vertex);
        } finally {
            graph.getModel().endUpdate();
        }
        resizeVertices();
        fixLayout();
        addToContentPane();
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
        Object vertex = graph.insertVertex(graphParent, null, name, 0, 0, 0, 0);
        vertices.add(vertex);
        return vertex;
    }

    private Object addEdge(Object parent, Object child) {
        Object edge = graph.insertEdge(graphParent, null, "", parent, child);
        return edge;
    }

}
