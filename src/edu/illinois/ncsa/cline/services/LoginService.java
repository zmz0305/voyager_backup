package edu.illinois.ncsa.cline.services;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;

import edu.illinois.ncsa.cline.voyager.Crawler4UI;
import edu.illinois.ncsa.core.aaa.AAAManager;
import edu.illinois.ncsa.core.aaa.Activities;
import edu.illinois.ncsa.core.aaa.ActivityLogger;
import edu.illinois.ncsa.core.aaa.DatabaseAAAManager;
import edu.illinois.ncsa.core.aaa.DatastoreException;
import edu.illinois.ncsa.core.shared.UserRecord;
import edu.illinois.ncsa.core.shared.names.AAANames;
import edu.illinois.ncsa.core.shared.names.DatabaseNames;
import edu.illinois.ncsa.core.shared.names.GeneralNames;
import edu.illinois.ncsa.core.shared.names.NCSANames;

/**
 * a class contains the functions for user login and logout
 * @author zmz0305
 */
public class LoginService {
    
    /** set when initialized. */
    private static boolean inited = false;
    
    /**
     * this should only be called once, it will initialize database connections
     * and so on, set up the connection handler the auth handler and stuff like that right 
     * there.
     * 
     * copied and modified from package edu.illinois.ncsa.cline.server.AAAServiceImpl.java;
     */
    synchronized public static void initdb() {
        if (!inited) {
            inited = true;
            // The stuff is stored in properties files.
            Properties p = new Properties();
            try {
                p.load(Crawler4UI.class.getResourceAsStream("speeddatabase.properties"));
            } catch (IOException e1) {
                p.put(DatabaseNames.URL.toString(), "jdbc:mysql://localhost:3306/speed");
                p.put(DatabaseNames.USER, "cline");
                p.put(DatabaseNames.PASSWORD, "~reallyhard~");
            }

            try {
                AAAManager.init(p);
                AAAManager aaa = AAAManager.getAAAManager();
                aaa.initializeManager(new Properties());
            } catch (DatastoreException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    
    
    /**
     * user login
     * copied and modified from package edu.illinois.ncsa.cline.server.AAAServiceImpl.java;
     * @param name
     * @param password
     * @return an UserRecord instance that contains the information of the logged in user
     * @throws IllegalArgumentException
     */
    public static UserRecord login(String name, String password) throws IllegalArgumentException {
        initdb();
        WrappedSession httpsession = VaadinSession.getCurrent().getSession();
        String sessionId = httpsession.getId();
        Map<NCSANames, Object> r = AAAManager.getAAAManager().authenticateUser(name, password, null, sessionId);
        if (r != null && r.containsKey(GeneralNames.ERROR)) {
            throw new IllegalArgumentException((String) r.get(GeneralNames.ERROR));
        } else {
            DatabaseAAAManager tmp = (DatabaseAAAManager) AAAManager.getAAAManager();
            try {
                UserRecord ur = tmp.fetchUserRecord(name);
                ur.sessionid = (String) r.get(AAANames.sessionid);
                httpsession.setAttribute(AAANames.clientid.toString(), r.get(AAANames.clientid));
                httpsession.setAttribute(AAANames.sessionid.toString(), r.get(AAANames.sessionid));
                httpsession.setAttribute(AAANames.username.toString(), r.get(AAANames.username));
                
                ActivityLogger.activity(name, Activities.LogIn, name + " logged in successfully.");
                return ur;
            } catch (DatastoreException e) {
                ActivityLogger.activity(name, Activities.LogInFailed, name + " could not log in because of a database error.");
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    /**
     * function for logout user
     * copied and modified from package edu.illinois.ncsa.cline.server.AAAServiceImpl.java;
     * @throws IllegalArgumentException
     */
    public static void logout() throws IllegalArgumentException {
        initdb();
        WrappedSession httpsession = VaadinSession.getCurrent().getSession();
        String username = (String) httpsession.getAttribute("username");
        String clientid = (String) httpsession.getAttribute("clientid");
        String sessionid = (String) httpsession.getAttribute("sessionid");
        
        AAAManager aaa = AAAManager.getAAAManager();
        Map<NCSANames, Object> r = aaa.logoutUser(username, clientid, sessionid);
        
        //?
        httpsession.invalidate();        
        if (r != null && r.containsKey(GeneralNames.ERROR)) {
            ActivityLogger.activity(username, Activities.LogOutFailed, username + " failed on logout : " + (String) r.get(GeneralNames.ERROR));
            throw new IllegalArgumentException((String) r.get(GeneralNames.ERROR));
        }
        ActivityLogger.activity(username, Activities.LogOut, username + " logged out successfully.");
    }
    
}
