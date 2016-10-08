package edu.illinois.ncsa.cline.voyager;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;

import edu.illinois.ncsa.cline.services.LoginService;

/**
 * the class for Menubar
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class CrawlMenubar extends MenuBar {
	/**
	 *  constructor
	 */
	public CrawlMenubar() {
	    addItem("Control", setControlCommand);
		addItem("Feeds", setFeedsCommand);
		addItem("Ingestions", setIngestionCommand);
		String name = (String)VaadinSession.getCurrent().getSession().getAttribute("username");
		addItem("Logout: " + name, setLogoutCommand);
		
		setWidth(100, Unit.PERCENTAGE);
	}

	/** command for ingestion button */
	MenuBar.Command setIngestionCommand = new MenuBar.Command() {
        
        @Override
        public void menuSelected(MenuItem selectedItem) {
            ((Crawler4UI)UI.getCurrent()).navigator.navigateTo("ingestion");
        }
    };
	
	/** command for the control button */
	MenuBar.Command setControlCommand = new MenuBar.Command() {
		public void menuSelected(MenuItem selectedItem) {
			((Crawler4UI)UI.getCurrent()).navigator.navigateTo("control");
		}
	};
	
	
	/** command for config button */
	MenuBar.Command setConfigCommand = new MenuBar.Command() {
		public void menuSelected(MenuItem selectedItem) {
		    ((Crawler4UI)UI.getCurrent()).navigator.navigateTo("config");
		}
	};
	
	/** command for feeds button */
	MenuBar.Command setFeedsCommand = new MenuBar.Command() {
		public void menuSelected(MenuItem selectedItem) {
		    ((Crawler4UI)UI.getCurrent()).navigator.navigateTo("feedslisting");
		}
	};
	
	
	/** command for logout button */
	MenuBar.Command setLogoutCommand = new MenuBar.Command() {
        
        @Override
        public void menuSelected(MenuItem selectedItem) {
            LoginService.logout();
            Page.getCurrent().setLocation("/crawler4");
        }
    };
    
    
}
