package core.util.Graphing;

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
import core.model.btree.task.NamedTask;

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

        this.mxGraph = new mxGraph();
        this.graphParent = mxGraph.getDefaultParent();

        this.component = createGraphComponent();
    }

    private mxGraphComponent createGraphComponent() {
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
}