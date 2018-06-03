package org.augbari.modules.positionTracker.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class Plotter extends ApplicationFrame {

    public XYSeries posX, posY, posZ;

    public Plotter(final String title) {

        super(title);
        posX = new XYSeries("X");
        posY = new XYSeries("Y");
        posZ = new XYSeries("Z");
        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(posX);
        data.addSeries(posY);
        data.addSeries(posZ);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Acceleration",
                "Time",
                "Magnitude",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1024, 768));
        setContentPane(chartPanel);

    }

}