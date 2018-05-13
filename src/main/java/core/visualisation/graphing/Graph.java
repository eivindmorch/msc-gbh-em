package core.visualisation.graphing;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import core.btree.tasks.modular.template.Task;

import java.util.ArrayList;

class  Graph {

    private Task root;
    private ArrayList<Object> vertices;
    private com.mxgraph.view.mxGraph mxGraph;
    private Object graphParent;
    private mxGraphComponent component;

    Graph(Task root) {
        this.root = root;

        this.vertices = new ArrayList<>();

        this.mxGraph = new mxGraph(Style.getStyleSheet());
        this.graphParent = mxGraph.getDefaultParent();

        this.component = createGraphComponent();
    }

    private mxGraphComponent createGraphComponent() {
        Task rootClone = root.cloneTask();

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

    private void graphSubtree(Task root, Object rootVertex) {

        for (Task child : root.getChildren()) {
            Object childVertex = addVertex(child);
            addEdge(rootVertex, childVertex);

            graphSubtree(child, childVertex);
        }
    }

    private Object addVertex(Task task) {
        String name = task.getDisplayName();
        String style = Style.getCellStyle(task);
        Object vertex = mxGraph.insertVertex(graphParent, null, name, 0, 0, 0, 0, style);
        vertices.add(vertex);
        return vertex;
    }

    private Object addEdge(Object parent, Object child) {
        Object edge = mxGraph.insertEdge(graphParent, null, "", parent, child);
        return edge;
    }
}