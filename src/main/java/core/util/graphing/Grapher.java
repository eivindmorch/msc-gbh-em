package core.util.graphing;

import core.BtreeAlt.TempTask;

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

    public static void quickGraph(TempTask... tempTasks) {
        GraphFrame graphFrame = Grapher.createNewFrame("Asd");
        GraphTab graphTab = new GraphTab("sdk");
        for (TempTask tempTask : tempTasks) {
            graphTab.add(tempTask);
        }
        graphFrame.addTab(graphTab);
        graphFrame.display();
    }

}
