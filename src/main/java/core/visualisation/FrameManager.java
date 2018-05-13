package core.visualisation;

import java.util.ArrayList;


public abstract class FrameManager {

    private static ArrayList<Frame> frameList = new ArrayList<>();

    public static Frame createNewFrame(String title) {
        Frame frame = new Frame(title);
        frameList.add(frame);
        return frame;
    }

    public static void closeAllFrames() {
        for (Frame frame : frameList) {
            frame.close();
        }
    }

//    public static void quickGraph(String title, Task... tasks) {
//        Frame frame = FrameManager.createNewFrame(title);
//        Tab tab = new Tab(title);
//        for (Task task : tasks) {
//            tab.add(task);
//        }
//        frame.addTab(tab);
//        frame.display();
//    }
//
//    public static void quickGraph(String title, com.badlogic.gdx.ai.btree.Task... tasks) {
//        Frame frame = FrameManager.createNewFrame(title);
//        Tab tab = new Tab(title);
//        for (com.badlogic.gdx.ai.btree.Task task : tasks) {
//            tab.add(task);
//        }
//        frame.addTab(tab);
//        frame.display();
//    }

}
