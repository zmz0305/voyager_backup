package edu.illinois.ncsa.cline.voyager.subwindows;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import org.vaadin.dialogs.ConfirmDialog;

import com.gargoylesoftware.htmlunit.javascript.host.dom.Text;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.illinois.ncsa.cline.database.CrawlStatsReader;
import edu.illinois.ncsa.cline.database.shared.FeedStats;
import edu.illinois.ncsa.cline.voyager.FeedsListing;

/**
 * modal for add feeds
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class AddFeedWindow extends Window {
    /** get the base directory */
    private String basepath = VaadinService.getCurrent().getBaseDirectory()
        .getAbsolutePath();

    /** resources for buttons */
    private FileResource cross = new FileResource(new File(basepath
        + "/WEB-INF/images/cross.png"));
    
    /** outter most wrapper layout */
    private VerticalLayout containerLayout = new VerticalLayout();
    
    /** textfiled for entering url */
    private TextField urlField = new TextField("RSS Feed URL");
    
    /** add button for final committing */
    private Button addButton = new Button("Save");
    
    /** cancel button */
    private Button cancelButton = new Button("Cancel");
    
    /** test button, click to test if the url is working */
    private Button testButton = new Button("Test");
    
    /** field to fill in the name of the publisher  */
    private TextField feed_enterer_textField = new TextField("Feed Enterer");
    
    /** combo box for feed type1 */
    private ComboBox feed_type1_comboBox; 
   
    /** combo box for feed type2*/
    private ComboBox feed_type2_comboBox;
    
    /** text field for geo spatial focus*/
    private TextField geo_spatial_focus_textField = new TextField("Geo-Spatial Focus");
    
    /** text field fro other focus */
    private TextField other_focus_textField = new TextField("Other Focus");
    
    /** field to fill in this feed's country */
    private TextField source_country_textField = new TextField("Source Country");
    
    /** text field for source outlet */
    private TextField source_outlet_textField = new TextField("Source Outlet");
    
    /** text field for title section */
    private TextField title_section_textField = new TextField("Title/Section");
    
    /** wrapper contains the url related stuff */
    private VerticalLayout urlVerticalLayout = new VerticalLayout();
    
    /** wrapper for add cancel and test button */
    private HorizontalLayout buttonWrapper = new HorizontalLayout();
    
    /** description label for user */
    private Label descriptionLabel = new Label("Test and Add button will be enabled when a valid "
        + "url starting with \"http://\" or \"https://\" is entered");
    
    /**
     * constructor
     * @param instance The feedslisting page instance that the window will operate on
     */
    public AddFeedWindow(FeedsListing instance) {
        setCaption("Add Feed");
        setWidth("450px");
        setHeight("640px");
        setModal(true);
        this.setClosable(false);
        this.setResizable(false);
        setStyleName("window-layout");
        setResizable(false);
        
        // add button action
        addButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String url = urlField.getValue();
                String enterer = feed_enterer_textField.getValue();
                String feedType1 = (String)feed_type1_comboBox.getValue();
                String feedType2 = (String)feed_type2_comboBox.getValue();
                String geoSpatialFocus = geo_spatial_focus_textField.getValue();
                String otherFocus = other_focus_textField.getValue();
                String sourceCountry = source_country_textField.getValue();
                String sourceOutlet = source_outlet_textField.getValue();
                String titleSection = title_section_textField.getValue();
                if (instance.getUniqueUrls().contains(url)) {
                    Notification.show("url already exist in database",
                        Type.WARNING_MESSAGE);
                } else {
                    try {
                    	String[] details = new String[9];
                    	details[0] = url;
                    	details[1] = enterer;
                    	details[2] = feedType1;
                    	details[3] = feedType2;
                    	details[4] = geoSpatialFocus;
                    	details[5] = otherFocus;
                    	details[6] = sourceCountry;
                    	details[7] = sourceOutlet;
                    	details[8] = titleSection;
                    	
                        // add into database
                        CrawlStatsReader.addFeed(details);

                        // add to unique checker
                        instance.getUniqueUrls().add(url);
                        
                        int rowid = CrawlStatsReader.getRowid(url);
                        // update cache
                        instance.getCache().addToCache(new FeedStats(url, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, rowid));

                        Button deleteButton = new Button(cross);
                        deleteButton.addStyleName("v-button-borderless");
                        deleteButton.addClickListener(new ClickListener() {

                            @Override
                            public void buttonClick(ClickEvent event) {
                                ConfirmDialog.show(UI.getCurrent(), "Confirm Delete", "Are you sure you want to delete?", "YES", "Cancel", new ConfirmDialog.Listener() {

                                    @Override
                                    public void onClose(ConfirmDialog arg0) {
                                        if (arg0.isConfirmed()) {
                                            instance.deleteAction(url);
                                        }
                                    }
                                });
                            }
                        });

                        // add to table
                        instance.getFeedslist().addItem(new Object[] {
                            new Label("<a href=" + url + ">" + url + "<a>",
                                ContentMode.HTML), 0, 0, 0, 0, 0, 0,
                            deleteButton }, url);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                close();
            }
        });
        cancelButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });

        // opener for popup window
        BrowserWindowOpener opener = new BrowserWindowOpener(urlField.getValue());

        // attach the opener to the test button, e.g. when click the button, window pops up
        opener.extend(testButton);
        
        // add listener for addField when value changes
        // so that test button will always attached to the newest url
        urlField.addTextChangeListener(new TextChangeListener() {
            
            @Override
            public void textChange(TextChangeEvent event) {
                String url = event.getText();
                opener.setUrl(url);
                if(!feedUrlChecker(url)){
                    addButton.setEnabled(false);
                    testButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                    testButton.setEnabled(true);
                }
            }
        });

        descriptionLabel.setWidth("100%");
        descriptionLabel.setHeightUndefined();
        urlField.setWidth("100%");
        
        // intialize options for feed type1
        ArrayList<String> feedTypes1 = new ArrayList<String>();
        feedTypes1.add("General");
        feedTypes1.add("Specialty");
        
        // initalize options for feed type2
        ArrayList<String> feedTypes2 = new ArrayList<String>();
        feedTypes2.add("None");
        feedTypes2.add("World");
        feedTypes2.add("Country");
        feedTypes2.add("Sub-national");
        feedTypes2.add("Regional");
        feedTypes2.add("Editorials");
        feedTypes2.add("Business");
        feedTypes2.add("Politics");
        feedTypes2.add("Security");
        feedTypes2.add("Law");
        feedTypes2.add("Humanitarian");
        feedTypes2.add("Miscellaneous");
        urlVerticalLayout.addComponent(descriptionLabel);
        urlField.setWidth("100%");
        urlField.setHeightUndefined();
        urlVerticalLayout.addComponent(urlField);
        urlVerticalLayout.setWidth("100%");
        urlVerticalLayout.setHeightUndefined();
        containerLayout.addComponent(urlVerticalLayout);
        feed_enterer_textField.setWidth("100%");
        urlVerticalLayout.addComponent(feed_enterer_textField);
        feed_type1_comboBox = new ComboBox("Feed Type1", feedTypes1);
        feed_type2_comboBox = new ComboBox("Feed Type2", feedTypes2);
        feed_type1_comboBox.setWidth("100%");
        feed_type2_comboBox.setWidth("100%");
        urlVerticalLayout.addComponent(feed_type1_comboBox);
        urlVerticalLayout.addComponent(feed_type2_comboBox);
        geo_spatial_focus_textField.setWidth("100%");
        other_focus_textField.setWidth("100%");
        source_country_textField.setWidth("100%");
        source_outlet_textField.setWidth("100%");
        title_section_textField.setWidth("100%");
        urlVerticalLayout.addComponent(geo_spatial_focus_textField);
        urlVerticalLayout.addComponent(other_focus_textField);
        urlVerticalLayout.addComponent(source_country_textField);
        urlVerticalLayout.addComponent(source_outlet_textField);
        urlVerticalLayout.addComponent(title_section_textField);
        buttonWrapper.addComponent(testButton);
        buttonWrapper.addComponent(addButton);
        buttonWrapper.addComponent(cancelButton);
        buttonWrapper.setSizeUndefined();
        buttonWrapper.setSpacing(true);
        containerLayout.addComponent(buttonWrapper);
        containerLayout.setComponentAlignment(buttonWrapper, Alignment.BOTTOM_RIGHT);
        containerLayout.setSizeFull();
        addButton.setEnabled(false);
        testButton.setEnabled(false);
        setContent(containerLayout);
    }

    
    /**
     * @param url the url to be tested
     * @return if the url fits requirement(start with http:// or https://)
     */
    private boolean feedUrlChecker(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
