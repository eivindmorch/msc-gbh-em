package core.util.graphing;

import com.badlogic.gdx.ai.btree.Task;
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

    public static void quickGraph(String title, TempTask... tempTasks) {
        GraphFrame graphFrame = Grapher.createNewFrame(title);
        GraphTab graphTab = new GraphTab(title);
        for (TempTask tempTask : tempTasks) {
            graphTab.add(tempTask);
        }
        graphFrame.addTab(graphTab);
        graphFrame.display();
    }

    public static void quickGraph(String title, Task... tasks) {
        GraphFrame graphFrame = Grapher.createNewFrame(title);
        GraphTab graphTab = new GraphTab(title);
        for (Task task : tasks) {
            graphTab.add(task);
        }
        graphFrame.addTab(graphTab);
        graphFrame.display();
    }

}
