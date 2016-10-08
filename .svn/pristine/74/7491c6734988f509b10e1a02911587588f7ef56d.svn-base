package edu.illinois.ncsa.cline.voyager.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.Background;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotBand;
import com.vaadin.addon.charts.model.PlotOptionsGauge;
import com.vaadin.addon.charts.model.TickPosition;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Component;

/**
 * gauge chart for performance measuring
 * @author zmz0305
 */
public class AngularGauge  {
    /**
     * get a chart for performance measuring
     * @param totalDownloads total number of doc downloaded
     * @param relevant number of relevant doc downloaded
     * @param irrelavent number of irrelevant doc downloaded
     * @param recent days for the needle
     * @return the chart
     */
    public static Component getChart(int totalDownloads, int relevant, int irrelavent, int recent) {
        final Chart chart = new Chart();
        chart.setSizeFull();

        final Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.GAUGE);
        configuration.getChart().setPlotBackgroundColor(null);
        configuration.getChart().setPlotBackgroundImage(null);
        configuration.getChart().setPlotBorderWidth(0);
        configuration.getChart().setPlotShadow(false);
        configuration.setTitle("Crawler Performance");
        configuration.setSubTitle("Green band represents relevant documents, "
            + "yellow band represent irrelevant documents, red represents all other documents. "
            + "The needle points to the number of documents downloaded for the given amount of days.");
        
        GradientColor gradient1 = GradientColor.createLinear(0, 0, 0, 1);
        gradient1.addColorStop(0, new SolidColor("#FFF"));
        gradient1.addColorStop(1, new SolidColor("#333"));

        GradientColor gradient2 = GradientColor.createLinear(0, 0, 0, 1);
        gradient2.addColorStop(0, new SolidColor("#333"));
        gradient2.addColorStop(1, new SolidColor("#FFF"));

        Background[] background = new Background[3];
        background[0] = new Background();
        background[0].setBackgroundColor(gradient1);
        background[0].setBorderWidth(0);
        background[0].setOuterRadius("109%");

        background[1] = new Background();
        background[1].setBackgroundColor(gradient2);
        background[1].setBorderWidth(1);
        background[1].setOuterRadius("107%");

        background[2] = new Background();
        background[2].setBackgroundColor(new SolidColor("#DDD"));
        background[2].setBorderWidth(0);
        background[2].setInnerRadius("103%");
        background[2].setOuterRadius("105%");

        configuration.getPane().setStartAngle(-150);
        configuration.getPane().setEndAngle(150);
        configuration.getPane().setBackground(background);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle("downloaded doc count"));
        yAxis.setMin(0);
        yAxis.setMax(totalDownloads);
        yAxis.setMinorTickInterval("auto");
        yAxis.setMinorTickWidth(1);
        yAxis.setMinorTickLength(10);
        yAxis.setMinorTickPosition(TickPosition.INSIDE);
        yAxis.setMinorTickColor(new SolidColor("#666"));
        yAxis.setGridLineWidth(0);
        yAxis.setTickPixelInterval(45);
        yAxis.setTickWidth(2);
        yAxis.setTickPosition(TickPosition.INSIDE);
        yAxis.setTickLength(10);
        yAxis.setTickColor(new SolidColor("#666"));

        yAxis.getLabels().setStep(2);
        yAxis.getLabels().setRotationPerpendicular();

        PlotBand[] plotBands = new PlotBand[3];
        plotBands[0] = new PlotBand(0, relevant, new SolidColor("#55BF3B"));
        plotBands[1] = new PlotBand(relevant, relevant+irrelavent, new SolidColor("#DDDF0D"));
        plotBands[2] = new PlotBand(relevant+irrelavent, totalDownloads, new SolidColor("#DF5353"));
        yAxis.setPlotBands(plotBands);

        final ListSeries series = new ListSeries("recent downloads", recent);
        PlotOptionsGauge plotOptions = new PlotOptionsGauge();
        plotOptions.getTooltip().setValueSuffix(" counts");
        series.setPlotOptions(plotOptions);
        configuration.setSeries(series);

        chart.drawChart(configuration);
        return chart;
    }
}