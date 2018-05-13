package core.util.graphing;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxStylesheet;
import com.sun.javaws.exceptions.InvalidArgumentException;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.LeafTasks.TempConditionTask;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempTask;
import core.btree.BehaviorTreeUtil;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;

import java.util.ArrayList;
import java.util.Hashtable;

abstract class Style {

    private static mxStylesheet styleSheet = createStyleSheet();

    private static final String COMPOSITE = "COMPOSITE";
    private static final String CONDITION = "CONDITION";
    private static final String ACTION = "ACTION";

    static mxStylesheet getStyleSheet() {
        return styleSheet;
    }

    private static mxStylesheet createStyleSheet() {
        mxStylesheet stylesheet = new mxStylesheet();
        stylesheet.putCellStyle(COMPOSITE, createCompositeStyle());
        stylesheet.putCellStyle(CONDITION, createConditionStyle());
        stylesheet.putCellStyle(ACTION, createActionStyle());
        stylesheet.setDefaultEdgeStyle(createEdgeStyle());
        return stylesheet;
    }

    private static Hashtable<String, Object> createCompositeStyle(){
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_PERIMETER, mxPerimeter.RectanglePerimeter);

        style.put(mxConstants.STYLE_SPACING_TOP, 3);

        style.put(mxConstants.STYLE_FILLCOLOR, "#D4E1F5");
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");

        style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        style.put(mxConstants.STYLE_FONTFAMILY, "Times New Roman");
        style.put(mxConstants.STYLE_FONTSIZE, 15);
        return style;
    }

    private static Hashtable<String, Object> createActionStyle() {
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_PERIMETER, mxPerimeter.RectanglePerimeter);

        style.put(mxConstants.STYLE_SPACING_TOP, 3);

        style.put(mxConstants.STYLE_FILLCOLOR, "#D5E8D4");
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");

        style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        style.put(mxConstants.STYLE_FONTFAMILY, "Times New Roman");
        style.put(mxConstants.STYLE_FONTSIZE, 15);
        return style;
    }

    private static Hashtable<String, Object> createConditionStyle() {
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_PERIMETER, mxPerimeter.EllipsePerimeter);

        style.put(mxConstants.STYLE_SPACING_TOP, 3);

        style.put(mxConstants.STYLE_FILLCOLOR, "#FFF2CC");
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");

        style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        style.put(mxConstants.STYLE_FONTFAMILY, "Times New Roman");
        style.put(mxConstants.STYLE_FONTSIZE, 15);
        return style;
    }

    private static Hashtable<String, Object> createEdgeStyle() {
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_ROUNDED, false);
        style.put(mxConstants.STYLE_ORTHOGONAL, false);
        style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ORTHOGONAL);

        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);

        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);

        style.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // default is #6482B9
        style.put(mxConstants.STYLE_FONTCOLOR, "#446299");

        return style;
    }

    static String getCellStyle(TempTask tempTask) {
        String style = null;
        if (tempTask instanceof TempCompositeTask) {
            style = COMPOSITE;
        } else if (tempTask instanceof TempConditionTask) {
            style = CONDITION;
        } else if (tempTask instanceof TempLeafTask) {
            style = ACTION;
        }
        return style;
    }

    static void resizeVertices(mxGraph mxGraph, ArrayList<Object> vertices) {
        mxGraph.getModel().beginUpdate();
        try {
            for (Object vertex : vertices) {
                mxCell cell = (mxCell) vertex;
                mxGeometry g = (mxGeometry) cell.getGeometry().clone();
                mxRectangle bounds = mxGraph.getView().getState(cell).getLabelBounds();
                g.setHeight(bounds.getHeight() + 15);
                g.setWidth(bounds.getWidth() + 20);
                mxGraph.cellsResized(new Object[]{cell}, new mxRectangle[]{g});
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }
    }

    static void fixLayout(mxGraph mxGraph, Object graphParent) {
        mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(mxGraph);

        treeLayout.setHorizontal(false);
        treeLayout.setEdgeRouting(true);
        treeLayout.setLevelDistance(40);

        treeLayout.execute(graphParent);
    }

    public static void main(String[] args) {
        Experiment1UnitInfo.init();
        try {
            Grapher.quickGraph("asd", BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 15, 30));
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
}
