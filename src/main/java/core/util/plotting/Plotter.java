package core.util.plotting;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public abstract class Plotter extends JPanel {

    public static ChartPanel getPlot(String title, XYSeriesCollection dataSet, String xLabel, String yLabel, boolean lines){
        XYPlot plot = new XYPlot();
        plot.setDataset(dataSet);

        // DOMAIN AND RANGE
        ValueAxis domain = new NumberAxis(xLabel);
        ValueAxis range = new NumberAxis(yLabel);

        domain.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        domain.setVisible(true);
        range.setVisible(true);

        plot.setDomainAxis(0, domain);
        plot.setRangeAxis(0, range);

        // DISPLAY AND STYLING
        plot.setDomainGridlinesVisible(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(lines, true);
        renderer.setAutoPopulateSeriesPaint(true);
        renderer.setBaseSeriesVisibleInLegend(true);
        plot.setRenderer(0, renderer);

        JFreeChart jFreeChart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        jFreeChart.getLegend().setPosition(RectangleEdge.TOP);
        TextTitle newTitle = new TextTitle(title, new Font("SansSerif", Font.BOLD, 16));
        newTitle.setPaint(Color.DARK_GRAY);
        jFreeChart.setTitle(newTitle);

        ChartPanel chartPanel = new ChartPanel(jFreeChart);
        chartPanel.setMaximumSize(new Dimension(1000, 600));
        chartPanel.setBorder(new EmptyBorder(10, 10, 40, 10));
        return chartPanel;
    }
}