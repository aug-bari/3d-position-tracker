package org.augbari.modules.positionTracker.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class Plotter extends ApplicationFrame {

    public XYSeries pos, acc, vel;

    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public Plotter(final String title) {

        super(title);
        pos = new XYSeries("Position");
        acc = new XYSeries("Acceleration");
        vel = new XYSeries("Speed");
        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(pos);
        data.addSeries(acc);
        data.addSeries(vel);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Z Axis",
                "Time",
                "Magnitude",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

}