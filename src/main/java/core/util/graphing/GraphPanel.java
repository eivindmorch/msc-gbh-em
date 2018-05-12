package core.util.graphing;

import com.badlogic.gdx.ai.btree.Task;
import com.mxgraph.swing.mxGraphComponent;
import core.BtreeAlt.TempTask;
import core.training.Chromosome;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GraphPanel extends JPanel {

    GraphPanel(String title, TempTask root) {
        this.setBorder(new EmptyBorder(15, 15, 0, 15));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JTextField thisTitle = new JTextField(title);
        thisTitle.setFont(thisTitle.getFont().deriveFont(14f));
        thisTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        this.add(thisTitle);

        this.setOpaque(true);
        this.setBackground(Color.DARK_GRAY);

        Graph graph = new Graph(root);
        mxGraphComponent mxGraphComponent = graph.getComponent();
        mxGraphComponent.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.add(mxGraphComponent);
    }

    GraphPanel(TempTask root) {
        this(root.toString(), root);
    }

    GraphPanel(Chromosome chromosome) {
        this(chromosome.toString(), chromosome.getBehaviourTreeRoot());
    }

    GraphPanel(String title, Task root) {
        this.setBorder(new EmptyBorder(15, 15, 0, 15));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JTextField thisTitle = new JTextField(title);
        thisTitle.setFont(thisTitle.getFont().deriveFont(14f));
        thisTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        this.add(thisTitle);

        this.setOpaque(true);
        this.setBackground(Color.DARK_GRAY);

        Graph2 graph = new Graph2(root);
        mxGraphComponent mxGraphComponent = graph.getComponent();
        mxGraphComponent.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.add(mxGraphComponent);
    }

    GraphPanel(Task root) {
        this(root.toString(), root);
    }
}
