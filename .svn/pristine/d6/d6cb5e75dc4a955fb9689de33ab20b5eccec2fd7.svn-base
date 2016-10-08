package edu.illinois.ncsa.cline.voyager.subwindows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.illinois.ncsa.cline.database.CrawlStatsReader;
import edu.illinois.ncsa.cline.database.shared.FeedStatsDaily;
import edu.illinois.ncsa.cline.helpers.TimeWindow;
import edu.illinois.ncsa.cline.helpers.feedstatsAttributes;
import edu.illinois.ncsa.cline.voyager.charts.MultiFeedStatsLine;

/**
 * this is the ultimate plot window, with configurable charts
 * @author zmz0305
 */
public class FeedStatsPlotWindow extends Window {

    /** wrapper for everything */
    VerticalLayout wrapper = new VerticalLayout();
    
    /** the session */
    WrappedSession httpSession = VaadinSession.getCurrent().getSession();
    
    /** frequency for time window */
    private long[] frequency = { 1000 * 60 * 60 * 24 };

    /** size for each frequency for time window */
    private int[] sizes = { 365 };
    
    /** a list of list of feedStatsDaily, e.g. for a feed: http://www.google.com, we have a list of 
     *  feedstats at various date (ArrayList<FeedStatsDaily>) and then for each feed we have this list
     */
    private ArrayList<ArrayList<FeedStatsDaily>> allFsdList = new ArrayList<ArrayList<FeedStatsDaily>>();

    /** outter list is for each feed, inner list is for each selected feedstatsAttribute of this feed */
    private ArrayList<ArrayList<TimeWindow>> windowsList = new ArrayList<ArrayList<TimeWindow>>();

    /** the list of chosen feeds' urls */
    private ArrayList<String> urls = new ArrayList<String>();
    
    /** the plot chart */
    private Component chart;
    
    /**
     * constructor 
     * @param rowids a set of rowids to display on chart
     */
    public FeedStatsPlotWindow(Set<Integer> rowids){
    	setWidth("80%");
    	setHeight("80%");
    	setModal(true);
        if(httpSession.getAttribute("plotSettings")==null){
            Notification.show("Error", "Select the feed attribute you want to show in plot settings first", Type.ERROR_MESSAGE);
            close();
        } else if(rowids.isEmpty()){
            Notification.show("Error", "Select the feed attribute you want to show in feeds list table first", Type.ERROR_MESSAGE);
            close();
        }
        
        ArrayList<feedstatsAttributes> selectedAttributes = (ArrayList<feedstatsAttributes>)httpSession.getAttribute("plotSettings");
        allFsdList.clear();
        urls.clear();
        windowsList.clear();
        ArrayList<FeedStatsDaily> fsdList;
        long end = System.currentTimeMillis();
        long start = end - 365 * 24 * 60 * 60 * 1000L;
        String url = new String();
        for (int id : rowids) {
            try {
                url = CrawlStatsReader.getUrl(id);
                fsdList = CrawlStatsReader.getStats(new Date(start), new Date(end), id, url);
                urls.add(url);
                allFsdList.add(fsdList);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        //configure all the timewindows
        for (ArrayList<FeedStatsDaily> fsds : allFsdList) {
            ArrayList<TimeWindow> windows = new ArrayList<TimeWindow>();
            for (feedstatsAttributes selectedAttribute : selectedAttributes) {
                TimeWindow window = new TimeWindow(frequency, sizes, start);
                for (FeedStatsDaily fs : fsds) {
                    long timestamp = fs.getDate().getTime();
                    switch (selectedAttribute) {
                        case scans:
                            window.update(fs.getScans(), timestamp);
                            break;
                        case changed:
                            window.update(fs.getChanged(), timestamp);
                            break;
                        case unchanged:
                            window.update(fs.getUnchanged(), timestamp);
                            break;
                        case failures:
                            window.update(fs.getFailures(), timestamp);
                            break;
                        case dup_urls:
                            window.update(fs.getDup_urls(), timestamp);
                            break;
                        case pages:
                            window.update(fs.getPages(), timestamp);
                            break;
                        case relevant:
                            window.update(fs.getRelevant(), timestamp);
                            break;
                        case irrelevant:
                            window.update(fs.getIrrelevant(), timestamp);
                            break;
                        case excluded:
                            window.update(fs.getExcluded(), timestamp);
                            break;
                        case empty:
                            window.update(fs.getEmpty(), timestamp);
                            break;
                        case unparsable:
                            window.update(fs.getUnparsable(), timestamp);
                            break;
                        case foreign_language:
                            window.update(fs.getForeign_language(), timestamp);
                            break;
                        case corrupted:
                            window.update(fs.getCorrupted(), timestamp);
                            break;
                        case duplicate:
                            window.update(fs.getDuplicate(), timestamp);
                            break;
                        case nocontent:
                            window.update(fs.getNocontent(), timestamp);
                            break;
                        case rowid:
                            window.update(fs.getRowid(), timestamp);
                            break;
                        default:
                            break;
                    }
                }
                windows.add(window);
            }
            windowsList.add(windows);
        }

        chart = MultiFeedStatsLine.getChart(windowsList, urls, selectedAttributes);
        setContent(chart);
    }
}
