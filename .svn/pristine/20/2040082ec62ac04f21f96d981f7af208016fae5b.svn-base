package edu.illinois.ncsa.cline.voyager.charts;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.LayoutDirection;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Component;

/**
 * this is a column bar chart for the performance measure in control page
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class StatsDayColumn {

    /**
     * @param endDate this is used to calculate the x axis tags
     * @param data a double array data containing all the counts of all attributes. 
     * @return
     */
    public static Component getChart(Date endDate, Integer[][] data) {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.setSizeFull();
        Configuration conf = chart.getConfiguration();

        conf.setTitle("Crawler Daily Performance");
        conf.setSubTitle("");

        XAxis x = new XAxis();
        long endDateMil = endDate.getTime();
        Date temp = new Date(endDateMil + 24*3600*1000l);
        temp.setHours(0);
        temp.setMinutes(0);
        temp.setSeconds(0);
        long tempMil = temp.getTime();
        Date d1 = new Date(tempMil-1000*24*3600l*1);
        Date d2 = new Date(tempMil-1000*24*3600l*2);
        Date d3 = new Date(tempMil-1000*24*3600l*3);
        Date d4 = new Date(tempMil-1000*24*3600l*4);
        Date d5 = new Date(tempMil-1000*24*3600l*5);
        Date d6 = new Date(tempMil-1000*24*3600l*6);
        Date d7 = new Date(tempMil-1000*24*3600l*7);
        SimpleDateFormat f = new SimpleDateFormat("M/d/YYYY");
        x.setCategories(f.format(d7) + " - " + f.format(d6), 
        		f.format(d6) + " - " + f.format(d5), 
        		f.format(d5) + " - " + f.format(d4), 
        		f.format(d4) + " - " + f.format(d3),
        		f.format(d3) + " - " + f.format(d2),
        		f.format(d2) + " - " + f.format(d1),
        		f.format(d1) + " - " + f.format(temp));
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("count");
        conf.addyAxis(y);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setBackgroundColor(new SolidColor(0, 0, 0, 0.0));
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(80);
        legend.setY(10);
        legend.setFloating(true);
        legend.setShadow(true);
        conf.setLegend(legend);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.x +': '+ this.series.name + this.y");
        conf.setTooltip(tooltip);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);
        ListSeries relevantListSeries = new ListSeries("relevant", data[0]);
        ListSeries irrelevantListSeries = new ListSeries("irrelevant", data[1]);
        ListSeries excludedListSeries = new ListSeries("excluded", data[2]);
        ListSeries otherListSeries = new ListSeries("other", data[3]);
        ListSeries dupListSeries = new ListSeries("dups", data[4]);
        conf.addSeries(relevantListSeries);
        conf.addSeries(irrelevantListSeries);
        conf.addSeries(excludedListSeries);
        conf.addSeries(dupListSeries);
        conf.addSeries(otherListSeries);
        
        chart.drawChart(conf);
        return chart;
    }
}