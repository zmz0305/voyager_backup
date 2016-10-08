package edu.illinois.ncsa.cline.voyager;

import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.illinois.ncsa.cline.helpers.FeedStatsCache;

/**
 * Configuration page view
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class CrawlerConfig extends AbsoluteLayout implements View {
    /** static variable */
    private final String destinationString = "/crawler4";
    
    /** configuration for current user */
    private Config config = (Config) VaadinSession.getCurrent().getSession().getAttribute("config");
    
    /** feedstats cache for current user */
    private FeedStatsCache cache = (FeedStatsCache) VaadinSession.getCurrent().getSession().getAttribute("cache");
    
    /** picker is a group of selections */
    private OptionGroup picker = new OptionGroup("Feeds Configuration");

    /** text field for entering the days */
    private TextField daysField = new TextField(null, "" + config.getDays());

    /** text field for entering weeks */
    private TextField weeksField = new TextField();

    /** date picker for start date */
    private PopupDateField startDateField = new PopupDateField();

    /** date picker for end date*/
    private PopupDateField endDateField = new PopupDateField();

    /** the right vertical layout */
    private VerticalLayout right = new VerticalLayout();

    /** the submit button for the date range option*/
    private Button rangeButton = new Button("submit");
    
    /** the submit button for the days option */
    private Button daysButton = new Button("submit");

    /** the submit button for the weeks option */
    private Button weeksButton = new Button("submit");

    /** the submit button for log config */
    private Button logButton = new Button("submit");
    
    /** the textbox for input path */
    private TextField logPathfField = new TextField(null, config.getLogPath());
    
    /** alert label for illegal input */
    private Label alert = new Label();

    /**
     * constructor
     */
    public CrawlerConfig() {
        // set  size
        setSizeFull();

        // add items to optionGroup, i.e. add radio buttons
        picker.addItem("Set a date range to display feeds");
        picker.addItem("Set number of weeks to display feeds");
        picker.addItem("Set number of days to display feeds");
        picker.addItem("Set log file location for displaying in control page");
        
        // set event changes to be immediately applied?
        picker.setImmediate(true);
        
        String rightLocationString = "left: 55%; top: 17%";
        
        // add listener for every time we click on different radiobutton,
        picker.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                // TODO Auto-generated method stub
                
                // get the value of the corresponding radio button
                String valueString = event.getProperty().getValue().toString();

                // behavior of the right side for different radiobutton choices
                if (valueString.equals("Set a date range to display feeds")) {
                    removeComponent(right);
                    right.removeAllComponents();
                    right.addComponent(new Label("from:"));
                    right.addComponent(startDateField);
                    right.addComponent(new Label("to:"));
                    right.addComponent(endDateField);
                    alert.setCaption("");
                    right.addComponent(alert);

                    rangeButton.addClickListener(new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            // TODO Auto-generated method stub
                            Date startDate = startDateField.getValue();
                            Date endDate = endDateField.getValue();
                            if (startDate == null || endDate == null) {
                                alert.setCaption("please set both start and end date!");
                            } else {
                                config.setToRange(
                                    startDate, endDate);
                                cache.reset();
                                Page.getCurrent().setLocation(destinationString);
                            }
                        }
                    });
                    right.addComponent(rangeButton);

                    addComponent(right, rightLocationString);
                } else if (valueString
                    .equals("Set number of weeks to display feeds")) {
                    removeComponent(right);
                    right.removeAllComponents();
                    right.addComponent(new Label("number of weeks:"));
                    right.addComponent(weeksField);
                    alert.setCaption("");
                    right.addComponent(alert);
                    right.addComponent(weeksButton);

                    weeksButton.addClickListener(new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            // TODO Auto-generated method stub
                            try {
                                int weeks = Integer.parseInt(weeksField.getValue());
                                config.setToDays(weeks * 7);
                                cache.reset();
                                Page.getCurrent().setLocation(destinationString);
                            }
                            catch (NumberFormatException e) {
                                alert.setCaption("please enter a number");
                            }
                        }
                    });
                    addComponent(right, rightLocationString);
                } else if (valueString
                    .equals("Set number of days to display feeds")) {
                    removeComponent(right);
                    right.removeAllComponents();
                    right.addComponent(new Label("number of days:"));
                    right.addComponent(daysField);
                    alert.setCaption("");
                    right.addComponent(alert);
                    right.addComponent(daysButton);

                    daysButton.addClickListener(new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            // TODO Auto-generated method stub
                            try {
                                int days = Integer.parseInt(daysField.getValue());
                                config.setToDays(days);
                                cache.reset();
                                Page.getCurrent().setLocation(destinationString);
                            }
                            catch (NumberFormatException e) {
                                alert.setCaption("please enter a number");
                            }

                        }
                    });
                    addComponent(right, rightLocationString);
                }
                else if (valueString.equals("Set log file location for displaying in control page")) {
                    removeComponent(right);
                    right.removeAllComponents();
                    right.addComponent(new Label("Log file path:"));
                    right.addComponent(logPathfField);
                    right.addComponent(alert);
                    right.addComponent(logButton);
                    
                    logButton.addClickListener(new ClickListener() {
                        
                        @Override
                        public void buttonClick(ClickEvent event) {
                            // TODO Auto-generated method stub
                            config.setLogPath(logPathfField.getValue());
                            alert.setCaption("path is set!");
                            Page.getCurrent().setLocation(destinationString);
                        }
                    });
                    addComponent(right, rightLocationString);
                }
            }
        });
        // set initial selected radio button
        picker.setValue("Set number of days to display feeds");
        addComponent(picker, "left: 20%; top: 20%");

    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
