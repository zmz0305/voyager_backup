package edu.illinois.ncsa.cline.voyager.subwindows;

import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.illinois.ncsa.cline.voyager.Config;
import edu.illinois.ncsa.cline.voyager.FeedsListing;

/**
 * sub window for filter settings
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class FeedFilterSettingsWindow extends Window {

    /** configuration for current user */
    private Config config = (Config) VaadinSession.getCurrent().getSession().getAttribute("config");

    /** picker is a group of selections */
    private OptionGroup picker = new OptionGroup("Feeds Configuration");

    /** text field for entering the days */
    private TextField daysField = new TextField("Enter the Days", "" + config.getDays());

    /** text field for entering weeks */
    private TextField weeksField = new TextField("Enter Weeks");
    
    /** date picker for start date */
    private PopupDateField startDateField = new PopupDateField("Start Date");

    /** date picker for end date*/
    private PopupDateField endDateField = new PopupDateField("End Date");

    /** the right vertical layout */
    private VerticalLayout container = new VerticalLayout();

    /** wrapper layout */
    private VerticalLayout wrapperLayout = new VerticalLayout();

    /** save the altered configuration. */
    private Button saveButton = new Button("Save");

    /** cancel the changes. */
    private Button cancelButton = new Button("Cancel");
    
    /** layout for buttons*/
    private HorizontalLayout buttonsLayout = new HorizontalLayout();
    
    /**
     * constructor
     * @param instance the passed in feedslisting instance
     */
    public FeedFilterSettingsWindow(FeedsListing instance) {
        setModal(true);
        setCaption("Filter Settings");
        setWidth("350px");
        setHeight("370px");
        setClosable(false);
        setResizable(false);
        setDraggable(false);
        
        // the wrapper layout must take up the full size.
        wrapperLayout.setSizeFull();
        setStyleName("window-layout");
        
        // configure the picker.
        picker.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {

                // get the value of the corresponding radio button
                String valueString = event.getProperty().getValue().toString();
                resetContainer(valueString);
            }
        });
        picker.addItem("Set a date range to display feeds");
        picker.addItem("Set number of weeks to display feeds");
        picker.addItem("Set number of days to display feeds");
        
        // set initial selected radio button
        picker.setValue("Set number of days to display feeds");
        picker.setSizeUndefined();
        wrapperLayout.addComponent(picker);
        
        // add the container.
        container.setSizeFull();
        container.setSpacing(true);
        wrapperLayout.addComponent(container);
        
        // add the buttons at the bottom
        this.buttonsLayout.setWidthUndefined();
        this.buttonsLayout.setSpacing(true);
        this.buttonsLayout.addComponent(cancelButton);
        this.buttonsLayout.addComponent(saveButton);
        this.wrapperLayout.addComponent(buttonsLayout);
        this.wrapperLayout.setComponentAlignment(this.buttonsLayout, Alignment.BOTTOM_RIGHT);
        setContent(wrapperLayout);
        cancelButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        
        saveButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String value = (String)picker.getValue();
                if (value.equals("Set a date range to display feeds")) {
                    Date startDate = startDateField.getValue();
                    Date endDate = endDateField.getValue();
                    if (startDate == null || endDate == null) {
                        Notification.show("Both the start date and the end date must be set.",
                            Notification.Type.WARNING_MESSAGE);
                    } else {
                        config.setToRange(startDate, endDate);
                        instance.getCache().reset();
                        instance.refreshTable(null);
                        close();
                    }
                } else if (value.equals("Set number of weeks to display feeds")) {
                    try {
                        int weeks = Integer.parseInt(weeksField.getValue());
                        config.setToDays(weeks * 7);
                        instance.getCache().reset();
                        instance.refreshTable(null);
                        close();
                    } catch (NumberFormatException e) {
                        Notification.show("The number of weeks must be an integer value.",
                            Notification.Type.WARNING_MESSAGE);
                    }
                } else {
                    try {
                        int days = Integer.parseInt(daysField.getValue());
                        config.setToDays(days);
                        instance.getCache().reset();
                        instance.refreshTable(null);
                        close();
                    } catch (NumberFormatException e) {
                        Notification.show("The number of days must be an integer value.",
                            Notification.Type.WARNING_MESSAGE);
                    }
                }
            }
        });
    }
    
    /** 
     * this method will replace the contents of the inner container with the 
     * editor controls required for the currently selected options.
     * @param valueString the selected item text.
     */
    private void resetContainer(final String valueString) {
        // behavior of the right side for different radiobutton choices
        if (valueString.equals("Set a date range to display feeds")) {
            container.removeAllComponents();
            container.addComponent(startDateField);
            container.addComponent(endDateField);
        } else if (valueString.equals("Set number of weeks to display feeds")) {
            container.removeAllComponents();
            container.addComponent(weeksField);
        } else if (valueString.equals("Set number of days to display feeds")) {
            container.removeAllComponents();
            container.addComponent(daysField);
        }
    }
}
