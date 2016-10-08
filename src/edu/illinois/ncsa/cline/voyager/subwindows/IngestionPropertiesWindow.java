package edu.illinois.ncsa.cline.voyager.subwindows;

import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.illinois.ncsa.cline.database.shared.IngestionLog;

/**
 * show the details of cooresponding ingestionlog
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class IngestionPropertiesWindow extends Window {

    /** outter most wrapper */
    private VerticalLayout wrapper = new VerticalLayout();
    
    /** text area for displaying all details */
    private RichTextArea textArea = new RichTextArea();

    /**
     * constructor for setting up the window
     * @param log the cooresponding ingestionlog
     */
    public IngestionPropertiesWindow(IngestionLog log) {
        setWidth("500px");
        setHeight("600px");
        setCaption("Log Properties");
        textArea.setSizeFull();
        textArea.setStyleName("no-toobar");
        textArea.setValue("<b>Name:</b> " + log.getName() + "<br><br>\n\n"
            + "<b>Owner:</b> " + log.getOwner() + "<br><br>\n\n"
            + "<b>Steward:</b> " + log.getSteward() + "<br><br>\n\n"
            + "<b>Source:</b> " + log.getSource() + "<br><br>\n\n"
            + "<b>Rights:</b> " + log.getRights() + "<br><br>\n\n"
            + "<b>Origination:</b> " + log.getOrigination() + "<br><br>\n\n"
            + "<b>Ingested Date:</b> " + log.getIngestedDate() + "<br><br>\n\n"
            + "<b>Begin Date:</b> " + log.getBeginDate() + "<br><br>\n\n"
            + "<b>End Date:</b> " + log.getEndDate() + "<br><br>\n\n"
            + "<b>Objectid:</b>" + log.getObjectId() + "<br><br>\n\n"
            + "<b>Description:</b> " + log.getDescription() + "<br><br>\n\n"
            + "<b>Populated Fields:</b> " + log.getPopulatedFields() + "<br><br>\n\n"
            );

        wrapper.setSizeFull();
        wrapper.addComponent(textArea);
        setContent(wrapper);
    }
}
