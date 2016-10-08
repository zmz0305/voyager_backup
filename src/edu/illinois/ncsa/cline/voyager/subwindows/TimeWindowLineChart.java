package edu.illinois.ncsa.cline.voyager.subwindows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import java_cup.internal_error;

import com.vaadin.ui.Window;

import edu.illinois.ncsa.cline.database.CrawlStatsReader;
import edu.illinois.ncsa.cline.database.shared.FeedStats;
import edu.illinois.ncsa.cline.database.shared.FeedStatsDaily;
import edu.illinois.ncsa.cline.helpers.TimeWindow;
import edu.illinois.ncsa.cline.voyager.charts.FeedStatsLine;

/**
 * this is a subwindow shows a chart representing many periods of time's data for a particular feed
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class TimeWindowLineChart extends Window {
    
    /** frequency for time window */
    private long[] frequency = {1000*60*60*24};
    
    /** size for each frequency for time window */
    private int[] sizes = {365};
    
    /** the start date of the time window*/
    private long startDate;
    
    /** time window for relavent field of feedstats */
    private TimeWindow relevantTimeWindown;
    
    /** time window for irrelavent field of FeedStats */
    private TimeWindow irrelaventTimeWindow; 
    
    /** time window for duplicates field of FeedStats */
    private TimeWindow duplicatesTimeWindow;
    
    /** time window for excluded field of FeedStats */
    private TimeWindow excludedTimeWindow;
    
    /**
     * constructor
     * @param url the corresponding url of that particular feed
     */
    public TimeWindowLineChart(String url, int rowid){
        
        setHeight("500px");
        setWidth("80%");
        //get the current time to calculate the startDate, which is one year from today
        long currentDate = System.currentTimeMillis();
        startDate = currentDate - 365L*24*60*60*1000;
        
        
        relevantTimeWindown = new TimeWindow(frequency, sizes, startDate);
        irrelaventTimeWindow = new TimeWindow(frequency, sizes, startDate);
        duplicatesTimeWindow = new TimeWindow(frequency, sizes, startDate);
        excludedTimeWindow = new TimeWindow(frequency, sizes, startDate);
        
        ArrayList<FeedStatsDaily> fsList;
        try {
            fsList = CrawlStatsReader.getStats(new Date(startDate), new Date(currentDate), rowid, url);
            long lastTime = 0;
            for(FeedStatsDaily fs:fsList){
                lastTime = fs.getDate().getTime();
                relevantTimeWindown.update(fs.getRelevant(), lastTime);
                irrelaventTimeWindow.update(fs.getIrrelevant(), lastTime);
                duplicatesTimeWindow.update(fs.getDuplicate(), lastTime);
                excludedTimeWindow.update(fs.getExcluded(), lastTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        TimeWindow[] data = {relevantTimeWindown, irrelaventTimeWindow, duplicatesTimeWindow, excludedTimeWindow};
        
        setContent(FeedStatsLine.getChart(data, url));
    }

    
    
    /**
     * wrapper function for update 4 timewindows
     * @param end
     * @param fs
     */
    private void updateTimeWindows(Date end, FeedStats fs) {
        relevantTimeWindown.update(fs.getRelevant(), end.getTime());
        irrelaventTimeWindow.update(fs.getIrrelevant(), end.getTime());
        duplicatesTimeWindow.update(fs.getDuplicate(), end.getTime());
        excludedTimeWindow.update(fs.getExcluded(), end.getTime());
    }
}
