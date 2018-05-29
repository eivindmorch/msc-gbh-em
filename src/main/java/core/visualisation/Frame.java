package core.visualisation;

import javax.swing.*;
import java.awt.*;

public class Frame implements Runnable {

    private JFrame jFrame;
    private JTabbedPane jTabbedPane;

    Frame(String title) {
        jFrame = new JFrame(title);
        jTabbedPane = new JTabbedPane();
    }

    public void display() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.add(jTabbedPane);
        showOnScreen(0, true);
        jFrame.setVisible(true);
    }

    public void addTab(Tab tab) {
        jTabbedPane.addTab(tab.getTitle(), tab.getPanel());
    }

    public void addTab(Tab tab, boolean setAsDefaultTab) {
        addTab(tab);
        if (setAsDefaultTab) {
            jTabbedPane.setSelectedIndex(jTabbedPane.getComponentCount() - 1);
        }
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
                jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.ICONIFIED);
            }
        } else if (graphicsDevices.length > 0) {
            jFrame.setLocation(graphicsDevices[0].getDefaultConfiguration().getBounds().x, jFrame.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
    }
}
