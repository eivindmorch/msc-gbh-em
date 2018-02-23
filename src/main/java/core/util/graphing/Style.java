package core.util.graphing;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxStylesheet;
import com.sun.javaws.exceptions.InvalidArgumentException;
import core.model.btree.BehaviorTreeUtil;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;

import java.util.Hashtable;

abstract class Style {

    private static mxStylesheet styleSheet = createStyleSheet();

    static mxStylesheet getStyleSheet() {
        return styleSheet;
    }

    private static mxStylesheet createStyleSheet() {
        mxStylesheet stylesheet = new mxStylesheet();
        stylesheet.putCellStyle("COMPOSITE", createCompositeStyle());
        stylesheet.putCellStyle("ACTION", createActionStyle());
        stylesheet.putCellStyle("CONDITION", createConditionStyle());
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

    public static void main(String[] args) {
        Experiment1UnitInfo.init();
        try {
            Grapher.quickGraph("asd", BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 5, 10));
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
}
