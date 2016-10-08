package edu.illinois.ncsa.cline.voyager.charts;

import java.util.Date;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Axis;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.DateTimeLabelFormats;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.ui.Component;

import edu.illinois.ncsa.cline.helpers.TimeWindow;

/**
 * line plot for feedstats monitoring
 * @author zmz0305
 */
public class FeedStatsLine {
    /**
     * return a configured, filled chart
     * @param data TimeWindow[] data = {relaventTimeWindow, irrelaventTimeWindow, duplicatesTimeWindow, excludedTimeWindow};
     * @param url the url of that feed
     * @return the chart
     */
    public static Component getChart(TimeWindow[] data, String url) {
        Chart chart = new Chart();
        chart.setHeight("450px");
        chart.setWidth("100%");

        Configuration configuration = chart.getConfiguration();
//        configuration.getChart().setType(ChartType.SPLINE);

        configuration.getTitle().setText(url);
        configuration.getSubTitle().setText("CrawlStats data");

        configuration.getTooltip().setFormatter("");

        configuration.getxAxis().setType(AxisType.DATETIME);
        configuration.getxAxis().setDateTimeLabelFormats(
            new DateTimeLabelFormats("%e. %b", "%b"));

        Axis yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle("number of times"));
        yAxis.setMin(0);

        configuration
            .getTooltip()
            .setFormatter("''+ this.series.name +' '+ Highcharts.dateFormat('%b. %e', this.x) +': '+ this.y");
        Object[][] data1 = new Object[358 / 7][2];
        
        loadSeries(data[0], configuration, data1, "relevant");
        loadSeries(data[1], configuration, data1, "irrelevant");
        loadSeries(data[2], configuration, data1, "duplicate");
        loadSeries(data[3], configuration, data1, "excluded");
        chart.drawChart(configuration);
        return chart;
    }

    /**
     * helper function to load data into chart
     * @param data data of a timewindow 
     * @param configuration the chart's configuration
     * @param data1  a empty already setted up bucket for data
     * @param setName the name for that set of data, the name will show in the legend of the chart
     */
    private static void loadSeries(TimeWindow data, Configuration configuration, Object[][] data1, String setName) {
        DataSeries ls = new DataSeries();
        ls.setPlotOptions(new PlotOptionsSpline());
        ls.setName(setName);
        Date end = new Date(System.currentTimeMillis());
        Date begin = new Date(end.getTime() - 358L * 24 * 60 * 60 * 1000);
        for (long i = 0; i < 358 / 7 - 1; i++) {
            data1[(int) i][0] = new Date(begin.getTime() + (i + 1) *7 * 24 * 60 * 60 * 1000L);
            data1[(int) i][1] = data.approximate(begin.getTime() + i * 24 * 60 * 60 * 1000L * 7, begin.getTime() + (i + 1) * 24 * 60 * 60 * 1000 * 7);
        }
        for (int i = 0; i < data1.length; i++) {
            Object[] ds = data1[i];
            if (ds[0] != null && ds[1] != null) {
                DataSeriesItem item = new DataSeriesItem((Date) ds[0],
                    (long) ds[1]);
                ls.add(item);
            }
        }

        configuration.addSeries(ls);
    }

}