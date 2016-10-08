package edu.illinois.ncsa.cline.voyager;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import edu.illinois.ncsa.cline.helpers.FeedStatsCache;
import edu.illinois.ncsa.cline.helpers.IngestionLogCache;
import edu.illinois.ncsa.cline.services.LoginService;
import edu.illinois.ncsa.core.shared.UserRecord;

/**
 * entry point
 * @author zmz0305
 */
@SuppressWarnings("serial")
@Theme("crawler4")
@StyleSheet({"vaadin://css/bootstrap.min.css"})
public class Crawler4UI extends UI {
    
    /**
     * @author zmz0305
     */
    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = true, ui = Crawler4UI.class, widgetset="edu.illinois.ncsa.cline.voyager.widgetset.VoyagerWidgetset")
    public static class Servlet extends VaadinServlet {
    }

    /** template frame for the website */
    ApplicationFraming applicationFrame = new ApplicationFraming();

    /** navigator for history and view */
    public Navigator navigator;
    
    
    /** configuration for instance */
    public Config config = new Config();
    
    /** button for login */
    private Button loginButton = new Button("Login");
    
    /** username field */
    private TextField username = new TextField("Username");
    
    /** password field*/
    private PasswordField password = new PasswordField("password");
    
    /** the container that holds login button and username&password fields */
    private VerticalLayout container = new VerticalLayout();
    
    /** outermost wrapper for layout*/
    private AbsoluteLayout loginWrapper = new AbsoluteLayout();
    
    @Override
    protected void init(VaadinRequest request) {
        WrappedSession httpsession = VaadinSession.getCurrent().getSession();
        setPollInterval(1000);
        //store config and cache in session, since they are specific per user
        if(httpsession.getAttribute("config") == null){
            Config config = new Config();
            httpsession.setAttribute("config", config);
        }
        if(httpsession.getAttribute("cache") == null){
            FeedStatsCache cache = new FeedStatsCache();
            httpsession.setAttribute("cache", cache);
        }
        if(httpsession.getAttribute("ingestionCache") == null){
            IngestionLogCache cache = new IngestionLogCache();
            httpsession.setAttribute("ingestionCache", cache);
        }
        
        
        // setup navigator for view control
        navigator = new Navigator(this, applicationFrame.getCenter());
        navigator.addView("control", new CrawlerControl());
        navigator.addView("", new CrawlerControl());
        navigator.addView("config", new CrawlerConfig());
        navigator.addView("feedslisting", new FeedsListing());
        navigator.addView("ingestion", new IngestionListing());
        
        // these two fields cannot be left blank
        username.setRequired(true);
        password.setRequired(true);
        username.focus();
        loginButton.addStyleName("loginButton");
        loginButton.setClickShortcut(KeyCode.ENTER);
        // put them into container
        container.addComponent(username);
        container.addComponent(password);
        container.addComponent(loginButton);
        
        // setup the panel and put it into wrapper
        Panel login = new Panel("Login", container);    
        login.setSizeUndefined();
        login.addStyleName("login");
        loginWrapper.setSizeFull();
        loginWrapper.addComponent(login, "left: 30%; top: 30%"); 
        
        // click action for the login button
        loginButton.addClickListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    UserRecord currentUser = LoginService.login(username.getValue(), password.getValue());
                    if(currentUser.isSupervisor()){
                        applicationFrame.refresh();
                        setContent(applicationFrame);
                    } else{
                        Notification.show("You do not have access to this site.", Type.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException e) {
                    Notification.show("Invalid username or password", Type.WARNING_MESSAGE);
                }
            }
        });
        
        // check if there is a logged in user
        if(httpsession.getAttribute("username") == null){
            setContent(loginWrapper);            
        } else {
            applicationFrame.refresh();
            setContent(applicationFrame);
        }       
    }
    
    
}