package edu.illinois.ncsa.cline.voyager.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import java_cup.internal_error;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AbstractPlotOptions;
import com.vaadin.addon.charts.model.Axis;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DashStyle;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.DateTimeLabelFormats;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.PlotOptionsArea;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Component;

import edu.illinois.ncsa.cline.helpers.TimeWindow;
import edu.illinois.ncsa.cline.helpers.feedstatsAttributes;

/**
 * for chart in feedlisting page
 * @author zmz0305
 */
public class MultiFeedStatsLine {
    /**
     * return a configured, filled chart
     * @param data TimeWindow[] data = {relaventTimeWindow, irrelaventTimeWindow, duplicatesTimeWindow, excludedTimeWindow};
     * @param url the url of that feed
     * @return the chart
     */
    public static Component getChart(ArrayList< ArrayList<TimeWindow> > data, ArrayList<String> urls, ArrayList<feedstatsAttributes> attrs) {
        ArrayList<AbstractPlotOptions> plotStyleList = new ArrayList<AbstractPlotOptions>();
        
        Chart chart = new Chart();
        chart.setHeight("100%");
        chart.setWidth("100%");

        Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.SPLINE);

        configuration.getTitle().setText("Crawler Statistics Plot");
        configuration.getSubTitle().setText("By clicking the legend icons at the bottom, you can enable/disable a particular line");

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
        
        for(int i = 0; i < data.size(); i++){
            SolidColor color = getRandomColor();
            for(int j = 0; j < data.get(0).size(); j++){
                DashStyle style = getDashStyle(j);
                loadSeries(data.get(i).get(j), configuration, data1, urls.get(i)+": "+attrs.get(j).toString(), color, style);                
            }
        }
        
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
    private static void loadSeries(TimeWindow data, Configuration configuration, Object[][] data1, String setName, SolidColor color, DashStyle style) {
        DataSeries ls = new DataSeries();
        PlotOptionsLine plotType = new PlotOptionsLine();
        plotType.setDashStyle(style);
        plotType.setColor(color);
        ls.setPlotOptions(plotType);
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
    
    private static DashStyle getDashStyle(int index){
        return DashStyle.values()[index];
    }
    
    private static SolidColor getRandomColor(){
        return new SolidColor((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
    }
}
