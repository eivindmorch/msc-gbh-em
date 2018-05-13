package core.visualisation.graphing;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import core.BtreeAlt.TempTask;

import java.util.ArrayList;

class  Graph {

    private TempTask root;
    private ArrayList<Object> vertices;
    private com.mxgraph.view.mxGraph mxGraph;
    private Object graphParent;
    private mxGraphComponent component;

    Graph(TempTask root) {
        this.root = root;

        this.vertices = new ArrayList<>();

        this.mxGraph = new mxGraph(Style.getStyleSheet());
        this.graphParent = mxGraph.getDefaultParent();

        this.component = createGraphComponent();
    }

    private mxGraphComponent createGraphComponent() {
        TempTask rootClone = root.cloneTask();

        mxGraph.getModel().beginUpdate();
        try {
            Object vertex = addVertex(rootClone);
            graphSubtree(rootClone, vertex);
        } finally {
            mxGraph.getModel().endUpdate();
        }
        Style.resizeVertices(mxGraph, vertices);
        Style.fixLayout(mxGraph, graphParent);
        return new mxGraphComponent(mxGraph);
    }

    mxGraphComponent getComponent() {
        return this.component;
    }

    private void graphSubtree(TempTask root, Object rootVertex) {

        for (TempTask child : root.getChildren()) {
            Object childVertex = addVertex(child);
            addEdge(rootVertex, childVertex);

            graphSubtree(child, childVertex);
        }
    }

    private Object addVertex(TempTask tempTask) {
        String name = tempTask.getDisplayName();
        String style = Style.getCellStyle(tempTask);
        Object vertex = mxGraph.insertVertex(graphParent, null, name, 0, 0, 0, 0, style);
        vertices.add(vertex);
        return vertex;
    }

    private Object addEdge(Object parent, Object child) {
        Object edge = mxGraph.insertEdge(graphParent, null, "", parent, child);
        return edge;
    }
}