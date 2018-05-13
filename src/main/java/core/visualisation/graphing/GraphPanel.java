package core.visualisation.graphing;

import com.mxgraph.swing.mxGraphComponent;
import core.btree.tasks.modular.template.Task;
import core.training.Chromosome;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class GraphPanel extends JPanel {

    GraphPanel(String title, Task root) {
        setup(title);

        TaskGraph taskGraph = new TaskGraph(root);
        mxGraphComponent mxGraphComponent = taskGraph.getComponent();
        mxGraphComponent.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.add(mxGraphComponent);
    }

    GraphPanel(Task root) {
        this(root.toString(), root);
    }

    GraphPanel(Chromosome chromosome) {
        this(chromosome.toString(), chromosome.getBehaviourTreeRoot());
    }

    GraphPanel(String title, com.badlogic.gdx.ai.btree.Task root) {
        setup(title);

        TaskExecGraph graph = new TaskExecGraph(root);
        mxGraphComponent mxGraphComponent = graph.getComponent();
        mxGraphComponent.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.add(mxGraphComponent);
    }

    GraphPanel(com.badlogic.gdx.ai.btree.Task root) {
        this(root.toString(), root);
    }

    private void setup(String title) {
        this.setBorder(new EmptyBorder(15, 15, 0, 15));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JTextField thisTitle = new JTextField(title);
        thisTitle.setFont(thisTitle.getFont().deriveFont(14f));
        thisTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        this.add(thisTitle);

        this.setOpaque(true);
        this.setBackground(Color.DARK_GRAY);
    }
}
