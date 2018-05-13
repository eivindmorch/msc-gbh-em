package core.visualisation;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Tab {

    private String title;
    private JPanel jPanel;

    public Tab(String title) {
        this.title = title;
        this.jPanel = new JPanel();
    }

    public Tab add(JPanel jPanel) {
        this.jPanel.add(jPanel);
        return this;
    }

    public Tab add(List<JPanel> jPanels) {
        for (JPanel jPanel : jPanels) {
            this.jPanel.add(jPanel);
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
