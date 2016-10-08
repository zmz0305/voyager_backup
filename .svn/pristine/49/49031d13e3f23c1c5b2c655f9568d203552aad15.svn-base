package edu.illinois.ncsa.cline.voyager.subwindows;

import java.util.ArrayList;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.illinois.ncsa.cline.helpers.feedstatsAttributes;
import edu.illinois.ncsa.cline.voyager.FeedsListing;

/**
 * Change the setting affecting the plotting characteristics.
 * @author redman
 */
public class PlotSettingWindow extends Window{
        
    /** default */
    private static final long serialVersionUID = 1L;

    /** wrapper for everything*/
    private VerticalLayout wrapper = new VerticalLayout();
    
    /** attribute select component */
    private TwinColSelect attrSelect = new TwinColSelect();
    
    /** the session */
    WrappedSession httpsession = VaadinSession.getCurrent().getSession();

    /** arraylist for storing the selected attributes */
    private ArrayList<feedstatsAttributes> selectedAttrs; 
    
    /** button wrapper */
    private HorizontalLayout buttonWrapper = new HorizontalLayout();
    
    /** confirm button */
    private Button plotButton = new Button("Plot");

    /** cancel button */
    private Button cancelButton = new Button("Cancel");
    
    /**
     * Provided the feeds listings.
     * @param instance
     */
    @SuppressWarnings("unchecked")
    public PlotSettingWindow(FeedsListing instance){
        setModal(true);
        setDraggable(false);
        this.setClosable(false);
        this.setResizable(false);
        setStyleName("window-layout");
        attrSelect.setSizeFull();
        wrapper.addComponent(attrSelect);
        wrapper.setExpandRatio(attrSelect, 1);
        
        buttonWrapper.setSizeUndefined();
        buttonWrapper.setSpacing(true);
        buttonWrapper.addStyleName("margin-top");
        buttonWrapper.addComponent(cancelButton);
        buttonWrapper.addComponent(plotButton);
        wrapper.addComponent(buttonWrapper);
        
        wrapper.setComponentAlignment(buttonWrapper, Alignment.BOTTOM_RIGHT);
        if(httpsession.getAttribute("plotSettings") == null){
            selectedAttrs = new ArrayList<feedstatsAttributes>();
        } else {
            selectedAttrs = (ArrayList<feedstatsAttributes>) httpsession.getAttribute("plotSettings");
        }
        wrapper.setSizeFull();
        
        // configure attrSelect
        attrSelect.setRows(12);
        attrSelect.setMultiSelect(true);
        attrSelect.setLeftColumnCaption("Available Attributes");
        attrSelect.setRightColumnCaption("Selected Attributes");
        for(feedstatsAttributes f:feedstatsAttributes.values()){
            attrSelect.addItem(f);
            attrSelect.setItemCaption(f, f.toString());  
        }
        attrSelect.removeItem(feedstatsAttributes.url);
        attrSelect.setValue(selectedAttrs);
        addListeners(instance);
        
        setWidth("340px");
        setHeight("434px");
        setContent(wrapper);
        setCaption("Plot Settings");
        setModal(true);
    }
        
    /**
     * add listeners wrapper functions
     * @param instance the feeds listing
     */
    @SuppressWarnings("serial")
    private void addListeners(FeedsListing instance){
        cancelButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        attrSelect.addValueChangeListener(new ValueChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChange(ValueChangeEvent event) {
                selectedAttrs.clear();
                Set<feedstatsAttributes> attrSet = (Set<feedstatsAttributes>)event.getProperty().getValue();
                for(feedstatsAttributes fa : attrSet){
                    selectedAttrs.add(fa);                    
                }
            }
        });
        plotButton.addClickListener(e -> {
            httpsession.setAttribute("plotSettings", selectedAttrs);
            if(instance.rowids.size() > instance.FEED_SELECTION_LIMIT){
                Notification.show("You cannot plot more than"+ instance.FEED_SELECTION_LIMIT + " at one time", Type.ERROR_MESSAGE);
            } else{
                FeedStatsPlotWindow psw = new FeedStatsPlotWindow(instance.rowids);
                psw.center();
                UI.getCurrent().addWindow(psw);
                close();
            }
        });
    }
}
