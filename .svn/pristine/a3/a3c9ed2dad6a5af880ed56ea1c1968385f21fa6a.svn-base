package edu.illinois.ncsa.cline.voyager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.vaadin.server.VaadinService;

/**
 * class for storing configurations
 * @author zmz0305
 */
public class Config {
    /** type = 0 means date range, 1 means days */
    private int type;

    /** begin date for date range */
    private Date beginDate;

    /** end date for date range */
    private Date endDate;

    /**	number of days */
    private int days;

    /** log path for log reader, init with a default value*/
    private String logPath = new String("./log");

    /** get the base directory */
    private String basepath = VaadinService.getCurrent().getBaseDirectory()
        .getAbsolutePath();
    
    /**
     * default constructor
     */
    public Config(){
        Properties p = new Properties();
        try {
            FileInputStream configFileInputStream = new FileInputStream(new File(basepath + "/WEB-INF/configurations.properties"));
            p.load(configFileInputStream);
            setLogPath(p.getProperty("LOGURL"));
            setDays(7);
            setType(1);
        } catch (IOException e1) {
            setLogPath(logPath);
            setDays(7);
            setType(1);
        }
    }
    
    /**
     * constructor for date range
     * @param beg begin date
     * @param end end date
     */
    public Config(Date beg, Date end) {
        setBeginDate(beg);
        setEndDate(end);
        setType(0);
    }

    /**
     * constructor for days
     * @param n number of days
     */
    public Config(int n) {
        setDays(n);
        setType(1);
    }

    /**
     * set config to range
     * @param beg begin date
     * @param end end date
     */
    public void setToRange(Date beg, Date end) {
        setBeginDate(beg);
        setEndDate(end);
        setType(0);
    }

    /**
     * set config to days
     * @param n n days
     */
    public void setToDays(int n) {
        setDays(n);
        setType(1);
    }

    /**
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * set type
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return begindate
     */
    public Date getBeginDate() {
        return beginDate;
    }

    /**
     * set begin date
     * @param beginDate
     */
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * @return end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * set end date
     * @param endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * get days
     * @return days
     */
    public int getDays() {
        return days;
    }

    /**
     * set days
     * @param days
     */
    public void setDays(int days) {
        this.days = days;
    }

    /**
     * get log path
     * @return log path
     */
    public String getLogPath() {
        return logPath;
    }

    /**
     * set log path
     * @param logPath
     */
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

}
