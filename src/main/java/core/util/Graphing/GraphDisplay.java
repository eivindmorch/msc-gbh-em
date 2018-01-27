package core.util.Graphing;

import com.badlogic.gdx.ai.btree.Task;
import com.mxgraph.swing.mxGraphComponent;
import core.training.Chromosome;
import core.training.Population;
import moeaframework.TestSolution;
import org.moeaframework.core.Solution;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class GraphDisplay extends JFrame implements Runnable {

    GraphDisplay(String title, Task root) {
        this.setTitle(title);
        this.setGraph(root);
        new Thread(this).start();
    }

    GraphDisplay(String title, Population population) {
        this.setTitle(title);
        // TODO
//        this.setGraphs(population);
        new Thread(this).start();
    }

    GraphDisplay(String title, org.moeaframework.core.Population population) {
        this.setTitle(title);
        this.setGraphs(population);
        new Thread(this).start();
    }

    @Override
    public void run() {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setMaximumSize(new Dimension(500, 500));
        showOnScreen(0, true);
        this.setVisible(true);
    }

    private void setGraph(Task task) {
        Graph graph = new Graph(task);
        this.add(new JScrollPane().add(graph.getComponent()));
    }

//    private void setGraphs(Population<?> population) {
//        JPanel outerPanel = new JPanel();
//        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
//
//        for (Chromosome chromosome : population.getChromosomes()) {
//            JPanel panel = new JPanel();
//            panel.setBorder(new EmptyBorder(15, 15, 0, 15));
//            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//
//            JTextField panelTitle = new JTextField(chromosome.toString());
//            panelTitle.setFont(panelTitle.getFont().deriveFont(14f));
//            panel.add(panelTitle);
//
//            panel.setOpaque(true);
//            panel.setBackground(Color.DARK_GRAY);
//
//            Graph graph = new Graph(chromosome.getBtree());
//            mxGraphComponent mxGraphComponent = graph.getComponent();
//            mxGraphComponent.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//            panel.add(mxGraphComponent);
//            outerPanel.add(panel);
//        }
//        JScrollPane jScrollPane = new JScrollPane(outerPanel);
//        this.setSize(1530, 1340);
//        this.add(jScrollPane);
//    }

    private void setGraphs(org.moeaframework.core.Population population) {
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));

        for (Solution solution : population) {
            TestSolution testSolution = (TestSolution) solution;

            JPanel panel = new JPanel();
            panel.setBorder(new EmptyBorder(15, 15, 0, 15));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JTextField panelTitle = new JTextField(testSolution.toString());
            panelTitle.setFont(panelTitle.getFont().deriveFont(14f));
            panelTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            panel.add(panelTitle);

            panel.setOpaque(true);
            panel.setBackground(Color.DARK_GRAY);

            Graph graph = new Graph(testSolution.getBtreeRoot());
            mxGraphComponent mxGraphComponent = graph.getComponent();
            mxGraphComponent.setBorder(new EmptyBorder(10, 10, 10, 10));

            panel.add(mxGraphComponent);
            outerPanel.add(panel);
        }
        JScrollPane jScrollPane = new JScrollPane(outerPanel);
//        this.setSize(1530, 1340);
        this.add(jScrollPane);
    }

    void close() {
        setVisible(false);
        dispose();
    }

    private void showOnScreen(int screen, boolean fullscreen) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
        if (screen > -1 && screen < graphicsDevices.length) {
            this.setLocation(graphicsDevices[screen].getDefaultConfiguration().getBounds().x, this.getY());
            if (fullscreen) {
                this.setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
            }
        } else if (graphicsDevices.length > 0) {
            this.setLocation(graphicsDevices[0].getDefaultConfiguration().getBounds().x, this.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
    }
}