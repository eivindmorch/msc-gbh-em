package core.util.Graphing;

import com.badlogic.gdx.ai.btree.Task;
import com.mxgraph.swing.mxGraphComponent;
import core.training.Chromosome;
import core.training.Population;

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
        this.setGraphs(population);
        new Thread(this).start();
    }

    @Override
    public void run() {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(1530, 500));
        this.setVisible(true);
    }

    private void setGraph(Task task) {
        Graph graph = new Graph(task);
        this.add(new JScrollPane().add(graph.getComponent()));
    }

    private void setGraphs(Population<?> population) {

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));

        for (Chromosome chromosome : population.getChromosomes()) {
            JPanel panel = new JPanel();
            panel.setBorder(new EmptyBorder(25, 25, 0, 25));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JTextField panelTitle = new JTextField(chromosome.toString());
            panelTitle.setFont(panelTitle.getFont().deriveFont(14f));
            panel.add(panelTitle);

            panel.setOpaque(true);
            panel.setBackground(Color.DARK_GRAY);


            Graph graph = new Graph(chromosome.getBtree());
            mxGraphComponent mxGraphComponent = graph.getComponent();
            mxGraphComponent.setBorder(new EmptyBorder(10, 10, 10, 10));

            panel.add(mxGraphComponent);
            outerPanel.add(panel);
        }
        JScrollPane jScrollPane = new JScrollPane(outerPanel);
        this.setSize(1530, 1340);
        this.add(jScrollPane);
    }

    void close() {
        setVisible(false);
        dispose();
    }
}