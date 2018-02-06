package core.util.graphing;

import javax.swing.*;
import java.awt.*;

public class GraphFrame implements Runnable {

    private JFrame jFrame;
    private JTabbedPane tabbedPane;

    GraphFrame(String title) {
        jFrame = new JFrame(title);
        tabbedPane = new JTabbedPane();
    }

    public void display() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.add(tabbedPane);
        showOnScreen(0, true);
        jFrame.setVisible(true);
    }

    public void addTab(GraphTab graphTab) {
        tabbedPane.addTab(graphTab.getTitle(), graphTab.getPanel());
    }

    void close() {
        jFrame.setVisible(false);
        jFrame.dispose();
    }

    private void showOnScreen(int screen, boolean fullscreen) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
        if (screen > -1 && screen < graphicsDevices.length) {
            jFrame.setLocation(graphicsDevices[screen].getDefaultConfiguration().getBounds().x, jFrame.getY());
            if (fullscreen) {
                jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            }
        } else if (graphicsDevices.length > 0) {
            jFrame.setLocation(graphicsDevices[0].getDefaultConfiguration().getBounds().x, jFrame.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
    }
}