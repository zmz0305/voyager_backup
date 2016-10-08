package edu.illinois.ncsa.cline.helpers;

import java.util.ArrayList;

import edu.illinois.ncsa.cline.aa.ArticleArchive;
import edu.illinois.ncsa.cline.database.shared.IngestionLog;

/**
 * cache for ingestionLog, for faster re-access
 * @author zmz0305
 */
public class IngestionLogCache {
    /** the cache data */
    private ArrayList<IngestionLog> data = new ArrayList<IngestionLog>();
    
    
    /**
     * reset cache data to null
     */
    public void reset(){
        data = null;
    }
    
    /**
     * refresh and create new data for cache
     */
    public void create(){
        try {
            data = ArticleArchive.get().getIngestionLogs();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
    }
    
    /** get the cache data
     * @return data in the cache
     */
    public ArrayList<IngestionLog> getData(){
        return data;
    }
    
    /**
     * add a new IngestionLog entry in cache
     * @param log the IngestionLog to be added
     */
    public void addToCache(IngestionLog log){
        data.add(new IngestionLog(log));
    }
}
