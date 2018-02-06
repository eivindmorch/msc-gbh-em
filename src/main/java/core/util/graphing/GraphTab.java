package core.util.graphing;

import core.training.Chromosome;
import core.training.Population;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class GraphTab {

    private String title;
    private JPanel jPanel;

    public GraphTab(String title) {
        this.title = title;
        this.jPanel = new JPanel();
    }

    public GraphTab add(Chromosome... chromosomes) {
        return add(Arrays.asList(chromosomes));
    }

    public GraphTab add(Population<?> population) {
        return add(population.getChromosomes());
    }

    public <L extends List<? extends Chromosome>> GraphTab add(L chromosomes) {

        for (Chromosome chromosome : chromosomes) {
            jPanel.add(new GraphPanel(chromosome));
        }
        return this;
    }
    
    JScrollPane getPanel() {
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.revalidate();
        jPanel.repaint();
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setMinimumSize(new Dimension(500, 500));
        return jScrollPane;
    }

    String getTitle() {
        return title;
    }
}