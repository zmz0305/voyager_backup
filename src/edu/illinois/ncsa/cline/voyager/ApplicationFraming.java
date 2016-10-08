package edu.illinois.ncsa.cline.voyager;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * base frame of the website
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class ApplicationFraming extends VerticalLayout {
    
	/** get the base directory */
	private String basepath = VaadinService.getCurrent().getBaseDirectory()
			.getAbsolutePath();

	/** get images source for right header */
	private FileResource rightBannerResource = new FileResource(new File(basepath
			+ "/WEB-INF/images/ClineCntrLogo.png"));
	
	/** get images source for left */
	private FileResource leftBannaerResource = new FileResource(new File(basepath
			+ "/WEB-INF/images/Voyager.png"));
	
	/** create the right header image */
	private Image rightBannerImage = new Image(null, rightBannerResource);
	
	/** create the left header image */
	private Image leftBannaerImage = new Image(null, leftBannaerResource);
	
	/** menu bar */
	private CrawlMenubar menu = new CrawlMenubar();
	
	/** footer label */
	private Label footerLabel = new Label(
			"Copyright 2015, Cline Center Voyager Portal, version 1.3.0");
	
	/** layout for the center part */
	private VerticalLayout center = new VerticalLayout();
	
	/** 2 col 1 row gridlayout for the header */
	private GridLayout header = new GridLayout(2, 1);

	/** menu container */
	private VerticalLayout menuContainer = new VerticalLayout();
	
	/**
	 * constructor to initialize the frame 
	 */
	public ApplicationFraming() {
		setSizeFull();
		setMargin(false);

		// set ratio for proper position of two header pics
		header.setColumnExpandRatio(0, 3);
		header.setColumnExpandRatio(1, 1);

		// add and setup two header pics
		header.addComponent(leftBannaerImage, 0, 0);
		header.addComponent(rightBannerImage, 1, 0);
		header.setComponentAlignment(leftBannaerImage, Alignment.TOP_LEFT);
		header.setComponentAlignment(rightBannerImage, Alignment.BOTTOM_CENTER);
		header.setWidth(100f, Unit.PERCENTAGE);
		header.setHeight(100f, Unit.PIXELS);
		header.setMargin(false);
		header.addStyleName("header");
		addComponent(header);
		
		
		// add and setup menubar
		menuContainer.setHeight(35, Unit.PIXELS);
		addComponent(menuContainer);

		//add and setup center part 
		center.setSizeFull();
		addComponent(center);

		// add footer
		footerLabel.setHeight(25, Unit.PIXELS);
		addComponent(footerLabel);
		setComponentAlignment(footerLabel, Alignment.BOTTOM_CENTER);
		
		//make the center part to expand all empty area
		setExpandRatio(center, 1f);
	}


	/**
	 * return the center part of the frame. 
	 * Mainly for the navigator because the second param of navigator's constructor
	 * is the place to put in view
	 * @return the center part
	 */
	public VerticalLayout getCenter(){
		return center;
	}

	/**
	 * render the correct menubar
	 */
	public void refresh(){
	    menuContainer.removeComponent(menu);
	    menu = new CrawlMenubar();
	    menuContainer.addComponent(menu);
	}

}
