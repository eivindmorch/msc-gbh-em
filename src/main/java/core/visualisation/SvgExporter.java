package core.visualisation;

import com.mxgraph.swing.mxGraphComponent;
import core.SystemSettings;
import core.btree.BehaviorTreeUtil;
import core.visualisation.graphing.Grapher;
import core.visualisation.plotting.Plotter;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.*;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.*;
import java.util.Random;

public abstract class SvgExporter {

    private static String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

    public static void exportGraphAsSvg(mxGraphComponent graphComponent, String filePath) throws IOException {
        SVGGraphics2D svgGenerator = createSvgGenerator();
        graphComponent.getGraphControl().drawGraph(svgGenerator, true);
        writeSvgFile(svgGenerator, filePath);
    }

    public static void exportChartAsSVG(ChartPanel chartPanel, String filePath) throws IOException {
        SVGGraphics2D svgGenerator = createSvgGenerator();
        Rectangle bounds = new Rectangle(chartPanel.getPreferredSize().width, chartPanel.getPreferredSize().height);
        chartPanel.getChart().draw(svgGenerator, bounds);
        writeSvgFile(svgGenerator, filePath);
    }

    private static SVGGraphics2D createSvgGenerator() {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(svgNS, "svg", null);
        return new SVGGraphics2D(document);
    }

    private static void writeSvgFile(SVGGraphics2D svgGenerator, String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(new File(filePath));
        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
        svgGenerator.stream(out, true);
        outputStream.flush();
        outputStream.close();
    }



    public static void main(String[] args) {
        testExportGraph();
        testExportChart();
    }

    private static void testExportGraph() {
        mxGraphComponent graphComponent = Grapher.getGraph(BehaviorTreeUtil.generateTestTree()).getMxGraphComponent();
        try {
            SvgExporter.exportGraphAsSvg(graphComponent, SystemSettings.RESOURCES_FILE_PATH + "svg/aasd.svg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testExportChart() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            XYSeries xySeries = new XYSeries("Test", true, true);

            double lastValue = 0;
            double value;

            for (int j = 0; j < 100; j++) {
                if (random.nextBoolean()) {
                    value = lastValue += (random.nextDouble() * 5);
                } else {
                    value = lastValue -= (random.nextDouble() * 5);
                }
                xySeries.add(j, value);
                lastValue = value;
            }
            xySeriesCollection.addSeries(xySeries);
        }

        ChartPanel chartPanel = Plotter.getPlot("Test", xySeriesCollection, "x label", "y label", true);
        try {
            SvgExporter.exportChartAsSVG(chartPanel, SystemSettings.RESOURCES_FILE_PATH + "svg/chart.svg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
