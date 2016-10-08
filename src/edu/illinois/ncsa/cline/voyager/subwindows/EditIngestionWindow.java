package edu.illinois.ncsa.cline.voyager.subwindows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
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
 * sub window for editing an ingestion log
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class EditIngestionWindow extends Window{
    /** get the base directory */
    private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    
    /** picture for add field button */
    private FileResource addFieldFileResource = new FileResource(new File(basepath + "/WEB-INF/images/plus.png"));
    
    /** picture for delete button */
    private FileResource deleteButtonFileResource = new FileResource(new File(basepath + "/WEB-INF/images/cross.png"));
    
    /** outtermost wrapper */
    private VerticalLayout wrapper = new VerticalLayout();

    /** button for adding action*/
    private Button addButton = new Button("Save");

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
    
    /** wrapper for populatedField stuff */
    private HorizontalLayout populatedFieldsGroup = new HorizontalLayout();
    
    /** table for field and its value */
    private Table addedFieldTable = new Table();
    
    /** button for add action */
    private Button addFieldButton = new Button(addFieldFileResource);
    
    /** the actual string of populated field will be displayed here */
    private Label populatedFieldsValuePreviewLabel = new Label("");
    
    /** the actual populated field */
    private String populatedFieldsValue = new String();
    
    /**
     * constructor
     * @param instance parent view instance
     * @param log the cooresponding ingestionlog
     */
    public EditIngestionWindow(IngestionListing instance, IngestionLog log) {
        setCaption("Add Ingestion Log");
        setWidth("500px");
        setHeight("700px");
        setStyleName("window-layout");
        setModal(true);
        
        configureComponents(log);
        addToWrapper();
        addListeners(instance, log);
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
     * configure components
     * @param log the cooresponding log
     */
    public void configureComponents(IngestionLog log) {
        wrapper.setMargin(true);

        addAndCancelButtonWrapper.addComponent(addButton);
        addAndCancelButtonWrapper.addComponent(cancelButton);
        addAndCancelButtonWrapper.setWidth("100%");
        addAndCancelButtonWrapper.setMargin(true);

        nameField.setInputPrompt("Name...");
        nameField.setValue(log.getName());
        nameField.setWidth("200px");
        ownerField.setInputPrompt("Owner...");
        ownerField.setValue(log.getOwner());
        ownerField.setWidth("200px");
        stewardField.setInputPrompt("Steward...");
        stewardField.setValue(log.getSteward());
        stewardField.setWidth("200px");
        descriptionField.setInputPrompt("Description");
        descriptionField.setValue(log.getDescription());
        descriptionField.setWidth("300px");
        sourceField.setInputPrompt("Source...");
        sourceField.setValue(log.getSource());
        sourceField.setWidth("300px");
        rightsField.setInputPrompt("Rights...");
        rightsField.setValue(log.getRights());
        rightsField.setWidth("200px");
        ingestionDateField.setInputPrompt("Create Date...");
        ingestionDateField.setValue(log.getIngestedDate());
        ingestionDateField.setWidth("200px");
        ;
        spanStartDateField.setInputPrompt("Start of time Span");
        spanStartDateField.setValue(log.getBeginDate());
        spanStartDateField.setWidth("200px");
        spanEndDateField.setInputPrompt("End of time span");
        spanEndDateField.setValue(log.getEndDate());
        spanEndDateField.setWidth("200px");
        originationField.setInputPrompt("Origination...");
        originationField.setValue(log.getOrigination());
        originationField.setWidth("200px");
                
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
        HashMap<String, String> existingFields = populatedFieldsParser(log);        
        for(String key:existingFields.keySet()){
            Button deleteButton = new Button(deleteButtonFileResource);
            deleteButton.addStyleName("v-button-borderless");
            deleteButton.addClickListener(new ClickListener() {
                
                @Override
                public void buttonClick(ClickEvent event) {
                    addedFieldTable.removeItem(key);
                    populatedFieldsValue = populatedFieldsValue.replace(key + " - " +existingFields.get(key) + "\n", "");
                    populatedFieldsValuePreviewLabel.setValue(populatedFieldsValue);
                }
            });
            addedFieldTable.addItem(
                new Object[]{
                    key,
                    existingFields.get(key),
                    deleteButton
                }
            , key);
            populatedFieldsValue += key + " - " + existingFields.get(key) + "\n";
            populatedFieldsValuePreviewLabel.setValue(populatedFieldsValue);
        }
    }

    /**
     * add listeners for components
     * @param instance the parent view instance
     * @param log the cooresponding log
     */
    public void addListeners(IngestionListing instance, IngestionLog log) {
        addButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                boolean valid = validateFields();
                if (valid) {
                    log.setName(nameField.getValue());
                    log.setOwner(ownerField.getValue());
                    log.setSteward(stewardField.getValue());
                    log.setDescription(descriptionField.getValue());
                    log.setSource(sourceField.getValue());
                    log.setRights(rightsField.getValue());
                    log.setIngestedDate(ingestionDateField.getValue());
                    log.setBeginDate(spanStartDateField.getValue());
                    log.setEndDate(spanEndDateField.getValue());
                    log.setOrigination(originationField.getValue());
                    log.setPopulatedFields(populatedFieldsValue);
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
                }
            }
        });
    }
    
    

    /**
     * validate if all fields are valid
     * @return check result
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
    
    /**
     * parse the populatedfield value into key value pairs, so that we can put them into the table
     * @param log the cooresponding log
     * @return a map containing parsed data of populatedfield
     */
    private HashMap<String, String> populatedFieldsParser(IngestionLog log){
        HashMap<String, String> pfHashMap = new HashMap<String, String>();
        String pfValue = log.getPopulatedFields();
        if(pfValue!=null && !pfValue.equals("")){
            try {
                String token = "\n";
                String[] pairs = pfValue.split(token);
                for(String s : pairs){
                    String[] pair = s.split(" - ");
                    pfHashMap.put(pair[0], pair[1]);
                }
            } catch (Exception e) {
                Notification.show("Please check populated field format of "+log.getObjectId(), Type.ERROR_MESSAGE);;
            }
            
        }
        
        return pfHashMap;
    }
}
