package core.visualisation.graphing;

import core.btree.tasks.modular.template.Task;

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

    public static void quickGraph(String title, Task... tasks) {
        GraphFrame graphFrame = Grapher.createNewFrame(title);
        GraphTab graphTab = new GraphTab(title);
        for (Task task : tasks) {
            graphTab.add(task);
        }
        graphFrame.addTab(graphTab);
        graphFrame.display();
    }

    public static void quickGraph(String title, com.badlogic.gdx.ai.btree.Task... tasks) {
        GraphFrame graphFrame = Grapher.createNewFrame(title);
        GraphTab graphTab = new GraphTab(title);
        for (com.badlogic.gdx.ai.btree.Task task : tasks) {
            graphTab.add(task);
        }
        graphFrame.addTab(graphTab);
        graphFrame.display();
    }

}
