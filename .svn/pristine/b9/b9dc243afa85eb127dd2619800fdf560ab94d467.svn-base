package edu.illinois.ncsa.cline.helpers;

import java.util.Date;

import edu.illinois.ncsa.cline.database.shared.DocumentContent;
import edu.illinois.ncsa.cline.database.shared.SolrFields;

/**
 * this class is used to transform DocumentContent which is a dictionary, into a class that can be used
 * as a vaadin data container
 * @author zmz0305
 */
public class DocumentContentAsBean {
    /** source_name in SolrFields */
    private String source_name;

    /** source_location in SolrFields */
    private String source_location;
    
    /** publication_Date in SolrFields */
    private Date publication_Date;
    
    /** original_language in SolrFields */
    private String original_language;
    
    /** byline_city in SolrFields */
    private String byline_city;
    
    /** archive_location in SolrFields */
    private String archive_location;
    
    /** content in SolrFields */
    private String content;
    
    /** mimetype in SolrFields */
    private String mimetype;
    
    /** title in SolrFields */
    private String title;
    
    /** original_docid in SolrFields */
    private String original_docid;
    
    /** original_source in SolrFields */
    private String original_source;
    
    /** original_fullreference in SolrFields */
    private String original_fullreference; 

    /**
     * constructor that takes a documentContent then transfor it into a Bean item type
     * @param dc
     */
    public DocumentContentAsBean(DocumentContent dc){
        this.content = (String)dc.get(SolrFields.content);
        this.title = (String)dc.get(SolrFields.title);
        this.setOriginal_docid((String)dc.get(SolrFields.original_docid));
        this.setOriginal_fullreference((String)dc.get(SolrFields.original_fullreference));
        this.setOriginal_language((String)dc.get(SolrFields.original_language));
        this.setOriginal_source((String)dc.get(SolrFields.original_source));
        this.setSource_name((String)dc.get(SolrFields.source_name));
        this.setSource_location((String)dc.get(SolrFields.source_location));
        this.setByline_city((String)dc.get(SolrFields.byline_city));
        this.setMimetype((String)dc.get(SolrFields.mimetype));
        this.setArchive_location((String)dc.get(SolrFields.archive_location));
        this.setPublication_Date((Date)dc.get(SolrFields.publication_date));
    }
    
    /**
     * for test
     * @param args
     */
    public static void main(String[] args) {
        for(SolrFields s : SolrFields.values())
        System.out.print( "\""+s+"\"" + ",");
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the source_name
     */
    public String getSource_name() {
        return source_name;
    }

    /**
     * @param source_name the source_name to set
     */
    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    /**
     * @return the source_location
     */
    public String getSource_location() {
        return source_location;
    }

    /**
     * @param source_location the source_location to set
     */
    public void setSource_location(String source_location) {
        this.source_location = source_location;
    }

    /**
     * @return the publication_Date
     */
    public Date getPublication_Date() {
        return publication_Date;
    }

    /**
     * @param publication_Date the publication_Date to set
     */
    public void setPublication_Date(Date publication_Date) {
        this.publication_Date = publication_Date;
    }

    /**
     * @return the original_language
     */
    public String getOriginal_language() {
        return original_language;
    }

    /**
     * @param original_language the original_language to set
     */
    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    /**
     * @return the byline_city
     */
    public String getByline_city() {
        return byline_city;
    }

    /**
     * @param byline_city the byline_city to set
     */
    public void setByline_city(String byline_city) {
        this.byline_city = byline_city;
    }

    /**
     * @return the archive_location
     */
    public String getArchive_location() {
        return archive_location;
    }

    /**
     * @param archive_location the archive_location to set
     */
    public void setArchive_location(String archive_location) {
        this.archive_location = archive_location;
    }

    /**
     * @return the mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * @param mimetype the mimetype to set
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    /**
     * @return the original_docid
     */
    public String getOriginal_docid() {
        return original_docid;
    }

    /**
     * @param original_docid the original_docid to set
     */
    public void setOriginal_docid(String original_docid) {
        this.original_docid = original_docid;
    }

    /**
     * @return the original_source
     */
    public String getOriginal_source() {
        return original_source;
    }

    /**
     * @param original_source the original_source to set
     */
    public void setOriginal_source(String original_source) {
        this.original_source = original_source;
    }

    /**
     * @return the original_fullreference
     */
    public String getOriginal_fullreference() {
        return original_fullreference;
    }

    /**
     * @param original_fullreference the original_fullreference to set
     */
    public void setOriginal_fullreference(String original_fullreference) {
        this.original_fullreference = original_fullreference;
    }

}
