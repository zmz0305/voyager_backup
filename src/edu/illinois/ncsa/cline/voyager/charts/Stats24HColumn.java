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

public class Stats24HColumn {
	/**
	 * @param endDate
	 *            this is used to calculate the x axis tags
	 * @param data
	 *            a double array data containing all the counts of all
	 *            attributes.
	 * @return
	 */
	public static Component getChart(Date endDate, Integer[][] data) {
		Chart chart = new Chart(ChartType.COLUMN);
		chart.setSizeFull();
		Configuration conf = chart.getConfiguration();

		conf.setTitle("Crawler Hourly Performance");
		conf.setSubTitle("");

		XAxis x = new XAxis();
		long endDateMil = endDate.getTime();
		Date endDateNew = new Date(endDateMil+3600*1000l);
		endDateNew.setMinutes(0);
		endDateNew.setSeconds(0);
		long endDateMilNew = endDateNew.getTime();
		// date for 24 hours
		Date[] hours = new Date[25];
		for(int i = 0; i < 25; i++){
			hours[i] = new Date(endDateMilNew - 1000*3600l*i);
		}
		
		// catagory names for each hour
		String[] cataHours = new String[24];
		for(int i = 0; i < 24; i++){
			SimpleDateFormat format = new SimpleDateFormat("Ka");
			cataHours[i] = format.format(hours[24 - i]) + " - " + format.format(hours[24 - i - 1]);
		}
		x.setCategories(cataHours);
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
		
		// setup tooltip
		Tooltip tooltip = new Tooltip();
		tooltip.setFormatter("this.x +': '+ this.series.name + ' ' + this.y");
		conf.setTooltip(tooltip);

		// setup series(colums)
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
