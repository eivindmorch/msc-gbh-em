package core.visualisation.graphing;

import com.badlogic.gdx.ai.btree.Task;
import core.training.Chromosome;
import core.training.Population;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Grapher {

    public static JPanel getGraph(Task task) {
        return new GraphPanel(task);
    }

    public static List<JPanel> getGraphs(Chromosome... chromosomes) {
        return getGraphs(Arrays.asList(chromosomes));
    }

    public static <L extends List<? extends Chromosome>> List<JPanel> getGraphs(L chromosomes) {
        List<JPanel> graphs = new ArrayList<>();
        for (Chromosome chromosome : chromosomes) {
            graphs.add(new GraphPanel(chromosome));
        }
        return graphs;
    }

    public static List<JPanel> getGraphs(Population<?> population) {
        return getGraphs(population.getChromosomes());
    }
}
