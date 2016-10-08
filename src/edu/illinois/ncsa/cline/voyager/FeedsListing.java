package edu.illinois.ncsa.cline.voyager;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import edu.illinois.ncsa.cline.database.CrawlStatsReader;
import edu.illinois.ncsa.cline.database.shared.FeedStats;
import edu.illinois.ncsa.cline.helpers.FeedStatsCache;
import edu.illinois.ncsa.cline.voyager.subwindows.AddFeedWindow;
import edu.illinois.ncsa.cline.voyager.subwindows.FeedFilterSettingsWindow;
import edu.illinois.ncsa.cline.voyager.subwindows.PlotSettingWindow;

/**
 * view for feeds listing
 * @author zmz0305
 */
/**
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class FeedsListing extends VerticalLayout implements View {
    
    /** limit how many feeds can be selected for plotting */
    public final int FEED_SELECTION_LIMIT = 10;

    /** configuration for current user */
    private Config config = (Config) VaadinSession.getCurrent().getSession().getAttribute("config");

    /** feedstats cache for current user */
    private FeedStatsCache cache = (FeedStatsCache) VaadinSession.getCurrent().getSession().getAttribute("cache");

    /** get the base directory */
    private String basepath = VaadinService.getCurrent().getBaseDirectory()
        .getAbsolutePath();
    
    /** resources for buttons */
    private FileResource cross = new FileResource(new File(basepath
        + "/WEB-INF/images/cross.png"));
    
    /** resources for buttons */
    private FileResource plus = new FileResource(new File(basepath + "/WEB-INF/images/plus.png"));

    /** resources for buttons */
    private FileResource flask = new FileResource(new File(basepath + "/WEB-INF/images/flask.png"));
    
    /** resources for buttons */
    private FileResource gear = new FileResource(new File(basepath + "/WEB-INF/images/gear.png"));
    
    /** the table containing feeds stats */
    private Table feedslist = new Table();

    /** panel contains addField and addFeedButton */
    private AbsoluteLayout addPanel = new AbsoluteLayout();

    /** textbox for add feed */
    private TextField filterField = new TextField();

    /** add feed button */
    private Button addFeedButton = new Button("Add Feed", plus);

    /** filter settings button */
    private Button filterSetting = new Button("Filter Settings", flask);

    /** plot settings button */
    private Button plotSettingButton = new Button("Plot", gear);
    
    /** eable edit mode button */
    private Button editModeButton = new Button("Enable Edit");

    /** used to make sure there is no dup urls in table */
    private HashSet<String> uniqueUrls = new HashSet<String>();

    /**data is the FeedStats data we get from database */
    private ArrayList<FeedStats> data = null;

    /** table selected rowids */
    public Set<Integer> rowids = new HashSet<Integer>();
    
    /** label for search textbox */
    private Label searchLabel = new Label("<h6>&#160&#160Search:&#160</h6>",ContentMode.HTML);
    
    /** show the count of listing */
    private Label listCountLabel = new Label("", ContentMode.HTML);
    /**
     * constructor
     */
    public FeedsListing() {
        setSizeFull();
        setupFeedslist();
        setupPlotStuff();
        addComponent(addPanel);
        addComponent(feedslist);
        setExpandRatio(feedslist, 1);
    }
    
    private void setupPlotStuff(){
    	plotSettingButton.setEnabled(false);
        plotSettingButton.addClickListener(e -> {
            PlotSettingWindow psw = new PlotSettingWindow(this);
            psw.center();
            UI.getCurrent().addWindow(psw);
        });
    }

    /**
     *  feedlist settings
     */
    private void setupFeedslist() {
        // set addPanel to full width
        addPanel.setHeight(37, Unit.PIXELS);
        FeedsListing instance = this;
        feedslist.setSelectable(true);
        feedslist.setMultiSelect(true);
        feedslist.setTableFieldFactory(new TableFieldFactory() {
            
            @Override
            public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                String pid = (String) propertyId;
                if(pid.equals("URL")){
                    Label item = (Label) itemId;
                    TextField field = new TextField();
                    field.setImmediate(true);
                    return field;
                }else{
                    TextField field = new TextField();
                    field.setImmediate(true);
                    return field;
                }
            }
        });
        // add click listener for addFeedButton
        addFeedButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                AddFeedWindow feedWindow = new AddFeedWindow(instance);
                feedWindow.center();
                UI.getCurrent().addWindow(feedWindow);
            }
        });

        // setup column headers
        feedslist.addContainerProperty("URL", Label.class, null);
        feedslist.addContainerProperty("Scan", Integer.class, null);
        feedslist.addContainerProperty("Fail", Integer.class, null);
        feedslist.addContainerProperty("Rel", Integer.class, null);
        feedslist.addContainerProperty("Irrel", Integer.class, null);
        feedslist.addContainerProperty("Excl", Integer.class, null);
        feedslist.addContainerProperty("Dups", Integer.class, null);
        feedslist.addContainerProperty("delete", Button.class, null);
        refreshTable(null);

        // listener for inputing filter text
        filterField.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                refreshTable(event.getText());
            }
        });
        
        // listener for selecting states entries
        feedslist.addValueChangeListener(e -> {
            Set<String> urlsSet = (Set<String>)e.getProperty().getValue();
            rowids.clear();
            for(String url : urlsSet){
                try {
                    int rowid = CrawlStatsReader.getRowid(url);
                    rowids.add(rowid);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if(rowids.size() == 0){
            	plotSettingButton.setEnabled(false);
            } else {
            	plotSettingButton.setEnabled(true);
            }
            
        });

        // set page length to 20
        // however it seems that the value is set automatically by table height,
        // except for length larger than the height (20 and 1 seems to behave
        // the same, but
        // if set to data.size(), then it would be obviously slower)
        feedslist.setPageLength(20);
        feedslist.setSizeFull();
        
        filterSetting.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                FeedFilterSettingsWindow settingsWindow = new FeedFilterSettingsWindow(instance);
                settingsWindow.center();
                UI.getCurrent().addWindow(settingsWindow);
            }
        });
        
        listCountLabel.setStyleName("list-count");
        listCountLabel.setValue("list count:"+data.size());
        filterField.setWidth("700px");
        filterField.setInputPrompt("Search...");
        addPanel.setWidth("100%");
        addPanel.addComponent(searchLabel);
        addPanel.addComponent(filterField, "left:60px");
        addPanel.addComponent(listCountLabel, "left:763px");
        addPanel.addComponent(addFeedButton, "right:250px");
        addPanel.addComponent(filterSetting, "right: 117px");
        addPanel.addComponent(plotSettingButton, "right: 40px");
        
    }

    /**
     * delete entry everywhere
     * @param url the key to identify which one to delete
     */
    public void deleteAction(String url) {
        try {
            CrawlStatsReader.dropFeed(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        feedslist.removeItem(url);
        uniqueUrls.remove(url);

        // update cache
        cache.deleteByUrl(url);
    }

    /**
     * getter method for uniqueUrls
     * @return uniqueUrls
     */
    public HashSet<String> getUniqueUrls() {
        return uniqueUrls;
    }

    /**
     * getter method for feedslist
     * @return feedslist
     */
    public Table getFeedslist() {
        return feedslist;
    }

    /**
     * to refresh the table content
     * @param filter the text filter for urls, if null then there is no filter
     */
    public void refreshTable(String filter) {
        try {
            // read config and get data accordingly
            if (cache.getData() != null) {
                data = cache.getData();
            } else if (config.getType() == 1) {
                cache.create(config.getDays());
                data = cache.getData();
            } else if (config.getType() == 0) {
                cache.create(config.getBeginDate(), config.getEndDate());
                data = cache.getData();
            }

        } catch (SQLException e) {
            Notification.show("error in reading FeedStats");
        }

        if (filter == null || filter.equals("")) {
            feedslist.removeAllItems();

            // add data into table
            for (int i = 0; i < data.size(); i++) {
                FeedStats temp = data.get(i);
                String id = temp.getUrl();
                uniqueUrls.add(temp.getUrl());

                Button deleteButton = new Button(cross);
                deleteButton.addStyleName("v-button-borderless");
                deleteButton.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        ConfirmDialog.show(UI.getCurrent(), "Confirm Delete", "Are you sure you want to delete?", "YES", "Cancel", new ConfirmDialog.Listener() {

                            @Override
                            public void onClose(ConfirmDialog arg0) {
                                if (arg0.isConfirmed()) {
                                    deleteAction(temp.getUrl());
                                }
                            }
                        });

                    }
                });


                feedslist.addItem(
                    new Object[] {
                        new Label("<a href=" + temp.getUrl() + ">"
                            + temp.getUrl() + "<a>", ContentMode.HTML),
                        temp.getScans(), temp.getFailures(),
                        temp.getRelevant(), temp.getIrrelevant(),
                        temp.getExcluded(), temp.getDuplicate(),
                        deleteButton }, id);
            }
        }
        else {
            feedslist.removeAllItems();
            for (int i = 0; i < data.size(); i++) {
                FeedStats temp = data.get(i);
                String id = new String(temp.getUrl());
                uniqueUrls.add(temp.getUrl());

                Button deleteButton = new Button(cross);
                deleteButton.addStyleName("v-button-borderless");
                deleteButton.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        ConfirmDialog.show(UI.getCurrent(), "Confirm Delete", "Are you sure you want to delete?", "YES", "Cancel", new ConfirmDialog.Listener() {

                            @Override
                            public void onClose(ConfirmDialog arg0) {
                                if (arg0.isConfirmed()) {
                                    deleteAction(temp.getUrl());
                                }
                            }
                        });
                    }
                });

                if (temp.getUrl().contains(filter)) {
                    feedslist.addItem(
                        new Object[] {
                            new Label("<a href=" + temp.getUrl() + ">"
                                + temp.getUrl() + "<a>", ContentMode.HTML),
                            temp.getScans(), temp.getFailures(),
                            temp.getRelevant(), temp.getIrrelevant(),
                            temp.getExcluded(), temp.getDuplicate(),
                            deleteButton}, id);
                }
            }

        }
    }

    /**
     * returns the feed list cache of this instance
     * @return cache instance
     */
    public FeedStatsCache getCache() {
        return cache;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
