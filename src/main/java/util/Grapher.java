package util;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.layout.mxCompactTreeLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grapher extends JFrame{

    ArrayList<Object> vertices;

    public Grapher() {
        super("Test");

        vertices = new ArrayList<>();

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();

        try {
            vertices.add(graph.insertVertex(parent, null, "?", 0, 0, 0, 0));
            vertices.add(graph.insertVertex(parent, null, "->", 0, 0, 0, 0));
            vertices.add(graph.insertVertex(parent, null, "Move", 0, 0, 0, 0));
            vertices.add(graph.insertVertex(parent, null, "World2!", 0, 0, 0, 0));
            vertices.add(graph.insertVertex(parent, null, "World2!", 0, 0, 0, 0));
            vertices.add(graph.insertVertex(parent, null, "World2!", 0, 0, 0, 0));
            Object e1 = graph.insertEdge(parent, null, "", vertices.get(0), vertices.get(1));
            Object e2 = graph.insertEdge(parent, null, "", vertices.get(0), vertices.get(2));
            Object e3 = graph.insertEdge(parent, null, "", vertices.get(1), vertices.get(3));
            Object e4 = graph.insertEdge(parent, null, "", vertices.get(1), vertices.get(4));
            Object e5 = graph.insertEdge(parent, null, "", vertices.get(1), vertices.get(5));
        } finally {
            graph.getModel().endUpdate();
        }

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

        // -- LAYOUT --
        mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(graph);
        treeLayout.setHorizontal(false);
        treeLayout.execute(parent);


        // -- COMPONENT --
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    public void graph(BehaviorTree btree) {
        // TODO
    }

    public static void main(String[] args) {
        Grapher grapher = new Grapher();
        grapher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        grapher.setMinimumSize(new Dimension(400, 400));
        grapher.setVisible(true);
    }

}
