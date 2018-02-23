package core.util.graphing;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxStylesheet;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.LeafTasks.TempConditionTask;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempTask;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

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
        resizeVertices();
        fixLayout();
        return new mxGraphComponent(mxGraph);
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

        String style = null;
        if (tempTask instanceof TempCompositeTask) {
            style = "COMPOSITE";
        } else if (tempTask instanceof TempConditionTask) {
            style = "CONDITION";
        } else if (tempTask instanceof TempLeafTask) {
            style = "ACTION";
        }

        Object vertex = mxGraph.insertVertex(graphParent, null, name, 0, 0, 0, 0, style);
        vertices.add(vertex);
        return vertex;
    }

    private Object addEdge(Object parent, Object child) {
        Object edge = mxGraph.insertEdge(graphParent, null, "", parent, child);
        return edge;
    }
}