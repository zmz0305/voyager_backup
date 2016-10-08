package edu.illinois.ncsa.cline.voyager.subwindows;

import java.util.ArrayList;
import java.util.Set;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.illinois.ncsa.cline.aa.ArticleArchive;
import edu.illinois.ncsa.cline.aa.StagingIterator;
import edu.illinois.ncsa.cline.database.shared.DocumentContent;
import edu.illinois.ncsa.cline.helpers.DocumentContentAsBean;

/**
 * ingestion detail subwindow
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class IngestionDetailWindow extends Window {

    /** outter most panel */
    private HorizontalSplitPanel wrapper = new HorizontalSplitPanel();
    
    /** left part wrapper */
    private VerticalLayout left = new VerticalLayout();
    
    /** right part wrapper */
    private VerticalLayout right = new VerticalLayout();

    /** container for document content */
    private BeanItemContainer<DocumentContentAsBean> container = new BeanItemContainer<DocumentContentAsBean>(DocumentContentAsBean.class);
    
    /** wrapper for container, in order to render button columns */
    private GeneratedPropertyContainer containerWrapper = new GeneratedPropertyContainer(container);
    
    /** ArrayList contains all documentContents that are supposed to be in the grid */
    private ArrayList<DocumentContent> dcList = new ArrayList<DocumentContent>();
    
    /** the area for detailed documentcontents info */
    private RichTextArea contentArea = new RichTextArea();
    
    /** the grid to display documentContents in dcList */
    private Grid documentList = new Grid();

    /**
     * constructor
     * @param objectId ingestion log's id
     */
    public IngestionDetailWindow(ObjectId objectId) {
        setWidth(90, Unit.PERCENTAGE);
        setHeight(90, Unit.PERCENTAGE);
        setModal(true);

        configureComponents(objectId);
        addToWrapper();
        addListeners();

        setContent(wrapper);
    }

    /**
     * wrapper helper function
     */
    public void addToWrapper() {
        wrapper.setFirstComponent(left);
        wrapper.setSecondComponent(right);
    }

    /**
     * wrapper helper function
     * @param objectId 
     */
    public void configureComponents(ObjectId objectId) {
        wrapper.setSplitPosition(30, Unit.PERCENTAGE);

        left.setSizeFull();
        left.addComponent(documentList);
        documentList.setSizeFull();
        
        right.setSizeFull();
        right.addComponent(contentArea);
        contentArea.setSizeFull();
        contentArea.addStyleName("no-toobar");
        
        documentList.setContainerDataSource(containerWrapper);
        
        documentList.removeAllColumns();
        documentList.addColumn("title");
        
        // array containing beans for document content
        ArrayList<DocumentContentAsBean> dcBeans = new ArrayList<DocumentContentAsBean>();
        try {
        		// array containing document content
                dcList = ArticleArchive.get().getStagingDataSample(objectId, 300);
                for (DocumentContent tempDc : dcList) {
                	
                	// add each document content into beans array
                    dcBeans.add(new DocumentContentAsBean(tempDc));
                }
                container.addAll(dcBeans);
        } catch (Exception e) {
            System.out.println("error in reading DocumentContents from mongodb");
            e.printStackTrace();
        }
    }

    /**
     * wrapper helper function
     */
    public void addListeners() {
    	
    	// listener triggered when selecting one document
        documentList.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                Set<Object> set = event.getSelected();
                for(Object obj : set){
                    DocumentContentAsBean bean = (DocumentContentAsBean)obj;
                    String content = "<b>Title:</b> " + bean.getTitle() + "<br><br>\n\n"
                                    + "<b>Original_docid:</b> " + bean.getOriginal_docid() + "<br><br>\n\n"
                                    + "<b>Original_fullreference:</b> " + bean.getOriginal_fullreference() + "<br><br>\n\n"
                                    + "<b>Original_language:</b> " + bean.getOriginal_language() + "<br><br>\n\n"
                                    + "<b>Original_source:</b> " + bean.getOriginal_source() + "<br><br>\n\n"
                                    + "<b>Source_name:</b> " + bean.getSource_name() + "<br><br>\n\n"
                                    + "<b>Source_location:</b> " + bean.getSource_location() + "<br><br>\n\n"
                                    + "<b>Byline_city:</b> " + bean.getByline_city() + "<br><br>\n\n"
                                    + "<b>Mimetype:</b> " + bean.getMimetype() + "<br><br>\n\n"
                                    + "<b>Archive_location:</b> " + bean.getArchive_location() + "<br><br>\n\n"
                                    + "<b>Publication_Date:</b> " + bean.getPublication_Date() + "<br><br>\n\n"
                                    + "<b>Content:</b> <br>\n" + bean.getContent() + "<br><br>\n\n";
                    contentArea.setValue(content);
                }
            }
        });
    }
    
    /**
     * we got as a value a BasicDBObject, which MUST be an array or multivalue field of some kind.
     * Arrays are represented by another BasicDBObject containing a hashmap where the key is the index
     * the values are the string values for the field.
     * @param object the database object
     * @return ArrayList containing the strings.
     */
    ArrayList<String> getArrayFromObject(BasicDBObject object) {
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0 ; i < object.size(); i++) {
            values.add((String)object.get(Integer.toString(i)));
        }
        return values;
    }
}


