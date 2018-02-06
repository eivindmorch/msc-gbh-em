package core.util.graphing;

import java.util.ArrayList;


public abstract class Grapher {

    private static ArrayList<GraphFrame> graphFrameList = new ArrayList<>();

    public static GraphFrame createNewFrame(String title) {
        GraphFrame graphFrame = new GraphFrame(title);
        graphFrameList.add(graphFrame);
        return graphFrame;
    }

    public static void closeAllGraphs() {
        for (GraphFrame graphFrame : graphFrameList) {
            graphFrame.close();
        }
    }
}
