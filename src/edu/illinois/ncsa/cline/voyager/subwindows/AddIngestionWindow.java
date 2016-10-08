package edu.illinois.ncsa.cline.voyager.subwindows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.illinois.ncsa.cline.aa.ArticleArchive;
import edu.illinois.ncsa.cline.database.shared.IngestionLog;
import edu.illinois.ncsa.cline.helpers.IngestionLogCache;
import edu.illinois.ncsa.cline.voyager.IngestionListing;

/**
 * subwindow for adding ingestions
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class AddIngestionWindow extends Window {
    /** get the base directory */
    private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    
    /** picture for add field button */
    private FileResource addFieldFileResource = new FileResource(new File(basepath + "/WEB-INF/images/plus.png"));
    
    /** picture for delete button */
    private FileResource deleteButtonFileResource = new FileResource(new File(basepath + "/WEB-INF/images/cross.png"));
    
    /** outtermost wrapper */
    private VerticalLayout wrapper = new VerticalLayout();

    /** button for adding action*/
    private Button addButton = new Button("Add");

    /** button for cancel and close window */
    private Button cancelButton = new Button("Cancel");

    /** wrapper for add and cancel button */
    private HorizontalLayout addAndCancelButtonWrapper = new HorizontalLayout();

    /** wrapper for all*/
    private VerticalLayout wrapperLayout = new VerticalLayout();
    
    /** ingestion cache */
    private IngestionLogCache cache = (IngestionLogCache)VaadinSession.getCurrent().getSession().getAttribute("ingestionCache");


    /** name field */
    private TextField nameField = new TextField("Name:");
    
    /** owner field */
    private TextField ownerField = new TextField("Owner:");
    
    /** steward field */
    private TextField stewardField = new TextField("Steward:");
    
    /** description field */
    private TextArea descriptionField = new TextArea("Description:");
    
    /** source field */
    private TextField sourceField = new TextField("Source:");
    
    /** rights field */
    private TextField rightsField = new TextField("Rights:");
    
    /** ingestion date field */
    private PopupDateField ingestionDateField = new PopupDateField("Ingested Date:");
    
    /** span start date field */
    private PopupDateField spanStartDateField = new PopupDateField("Start Date:");
    
    /** span end date field */
    private PopupDateField spanEndDateField = new PopupDateField("End Date:");
    
    /** origination field */
    private TextField originationField = new TextField("Origination: ");
    
    /** populated area */
    private ComboBox populatedFields;
    
    /** value for one designated field */
    private TextField populatedFieldValueField = new TextField("Value: ");
    
    /** wrapper for populatedfield stuff */
    private HorizontalLayout populatedFieldsGroup = new HorizontalLayout();
    
    /** the table for added field */
    private Table addedFieldTable = new Table();
    
    /** add field button */
    private Button addFieldButton = new Button(addFieldFileResource);
    
    /** this label displays the whole populated field string */
    private Label populatedFieldsValuePreviewLabel = new Label("");
    
    /** the whole populated field string */
    private String populatedFieldsValue = new String();
    
    /**
     * constructor
     * @param instance parent view instance
     */
    public AddIngestionWindow(IngestionListing instance) {
        setCaption("Add Ingestion Log");
        setWidth("500px");
        setHeight("700px");
        setStyleName("window-layout");
        setDraggable(false);
        setModal(true);
        
        addToWrapper();
        configureComponents();
        addListeners(instance);
        setContent(wrapper);
    }

    /**
     * add components to outter most wrapper layout
     */
    public void addToWrapper() {
        wrapper.addComponent(nameField);
        wrapper.addComponent(ownerField);
        wrapper.addComponent(stewardField);
        wrapper.addComponent(descriptionField);
        wrapper.addComponent(sourceField);
        wrapper.addComponent(rightsField);
        wrapper.addComponent(ingestionDateField);
        wrapper.addComponent(spanStartDateField);
        wrapper.addComponent(spanEndDateField);
        wrapper.addComponent(originationField);
        wrapper.addComponent(populatedFieldsGroup);
        wrapper.addComponent(populatedFieldsValuePreviewLabel);
        wrapper.addComponent(addedFieldTable);
        wrapper.addComponent(addAndCancelButtonWrapper);
    }

    /**
     * configure conponents
     */
    public void configureComponents() {
        wrapper.setMargin(true);

        addAndCancelButtonWrapper.addComponent(addButton);
        addAndCancelButtonWrapper.addComponent(cancelButton);
        addAndCancelButtonWrapper.setMargin(true);
        addAndCancelButtonWrapper.setSizeUndefined();
        addAndCancelButtonWrapper.setSpacing(true);
        nameField.setInputPrompt("Name...");
        nameField.setWidth("100%");
        ownerField.setInputPrompt("Owner...");
        ownerField.setWidth("100%");
        stewardField.setInputPrompt("Steward...");
        stewardField.setWidth("100%");
        descriptionField.setInputPrompt("Description");
        descriptionField.setWidth("100%");
        sourceField.setInputPrompt("Source...");
        sourceField.setWidth("100%");
        rightsField.setInputPrompt("Rights...");
        rightsField.setWidth("100%");
        ingestionDateField.setInputPrompt("Create Date...");
        ingestionDateField.setWidth("100%");
        spanStartDateField.setInputPrompt("Start of time Span");
        spanStartDateField.setWidth("100%");
        spanEndDateField.setInputPrompt("End of time span");
        spanEndDateField.setWidth("100%");
        originationField.setInputPrompt("Origination...");
        originationField.setWidth("100%");
        wrapper.setComponentAlignment(addAndCancelButtonWrapper, Alignment.BOTTOM_RIGHT);
        String[] fields = {"aid","source_name","source_id","source_url","source_host","source_location","publication_date","original_language","byline_city","ingest_date","archive_location","content","content_length","mimetype","url","url_host","title","title_length","geolocation","extracted_date","extracted_date_text","extracted_people","extracted_people_text","extracted_organizations","extracted_organizations_text","extracted_locations","extracted_locations_text","country","geolocation_probabilities","geolocation_featureids","geolocation_original","geolocation_locations","geolocation_locations_text","_id","original_docid","original_id1","original_id2","original_source","original_fullreference","original_regions","original_countries","original_topics"};
        ArrayList<String> pf = new ArrayList<String>();
        for(String s : fields){
            pf.add(s);
        }
        populatedFields = new ComboBox("Populated Field", pf);
        addFieldButton.addStyleName("v-button-borderless vertical-align-bottom");
        populatedFieldsGroup.addComponent(populatedFields);
        populatedFieldsGroup.addComponent(populatedFieldValueField);
        populatedFieldsGroup.addComponent(addFieldButton);
        addedFieldTable.addContainerProperty("Field", String.class, null);
        addedFieldTable.addContainerProperty("Value", String.class, null);
        addedFieldTable.addContainerProperty("Delete", Button.class, null);
        addedFieldTable.setWidth("100%");
        addedFieldTable.setHeight("200px");
    }

    /**
     * add listeners for components
     * @param instance the parent view instance
     */
    public void addListeners(IngestionListing instance) {
        addButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                boolean valid = validateFields();
                if (valid) {
                    IngestionLog log = new IngestionLog(null, nameField.getValue(),
                        ownerField.getValue(), stewardField.getValue(), descriptionField.getValue(),
                        sourceField.getValue(), rightsField.getValue(), ingestionDateField.getValue(),
                        spanStartDateField.getValue(), spanEndDateField.getValue(),
                        originationField.getValue(), populatedFieldsValue);
                    try {
                        ArticleArchive.get().updateIngestionLog(log);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    close();
                }
                else {
                    Notification.show("You must fill all fields before adding", Type.ERROR_MESSAGE);
                }
                cache.create();
                instance.refreshGrid();
            }
        });

        cancelButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });
        
        addFieldButton.addClickListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                String fieldNameString = (String)populatedFields.getValue();
                String fieldValueString = populatedFieldValueField.getValue();
                if(addedFieldTable.getItem(fieldNameString)!=null){
                    Notification.show(fieldNameString + " already exists!", Notification.Type.WARNING_MESSAGE);
                } else if (fieldValueString==null || fieldValueString.equals("")) {
                    Notification.show("Please fill the value field!");
                } else {
                    Button deleteButton = new Button(deleteButtonFileResource);
                    deleteButton.addStyleName("v-button-borderless");
                    deleteButton.addClickListener(new ClickListener() {
                        
                        @Override
                        public void buttonClick(ClickEvent event) {
                            addedFieldTable.removeItem(fieldNameString);
                            populatedFieldsValue = populatedFieldsValue.replace(fieldNameString + " - " +fieldValueString + "\n", "");
                            populatedFieldsValuePreviewLabel.setValue(populatedFieldsValue);
                        }
                    });
                    addedFieldTable.addItem(
                        new Object[]{
                            fieldNameString,
                            fieldValueString,
                            deleteButton
                        }
                    , (String)populatedFields.getValue());
                    populatedFieldsValue += "SolrFields." + fieldNameString + " - " + fieldValueString + "\n";
                    populatedFieldsValuePreviewLabel.setValue(populatedFieldsValue);
                    populatedFieldValueField.setValue("");
                    
                } 
            }
        });
    }
    
    

    /**
     * validate if all fields are valid
     * @return boolean value indicate if valid
     */
    private boolean validateFields() {
        boolean valid = true;
        if (nameField.getValue() == null || nameField.getValue().equals("")) {
            valid = false;
        }
        if (ownerField.getValue() == null || nameField.getValue().equals("")) {
            valid = false;
        }
        if (ownerField.getValue() == null || ownerField.getValue().equals("")) {
            valid = false;
        }
        if (stewardField.getValue() == null || stewardField.getValue().equals("")) {
            valid = false;
        }
        if (sourceField.getValue() == null || sourceField.getValue().equals("")) {
            valid = false;
        }
        if (spanStartDateField.getValue() == null || spanStartDateField.getValue().equals("")) {
            valid = false;
        }
        if (spanEndDateField.getValue() == null || spanEndDateField.getValue().equals("")) {
            valid = false;
        }
        if (originationField.getValue() == null || originationField.getValue().equals("")) {
            valid = false;
        }
        return valid;

    }

}
