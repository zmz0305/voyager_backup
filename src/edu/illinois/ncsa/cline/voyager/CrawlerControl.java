package edu.illinois.ncsa.cline.voyager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.illinois.ncsa.cline.database.CrawlStatsReader;
import edu.illinois.ncsa.cline.voyager.charts.Stats24HColumn;
import edu.illinois.ncsa.cline.voyager.charts.StatsDayColumn;

/**
 * the control view
 * 
 * @author zmz0305
 */
@SuppressWarnings("serial")
public class CrawlerControl extends VerticalLayout implements View {
	/** get the base directory */
	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

	/** configuration for current user */
	private Config config = (Config) VaadinSession.getCurrent().getSession().getAttribute("config");

	/** image resources */
	private FileResource startImageFileResource = new FileResource(new File(basepath + "/WEB-INF/images/control.png"));
	
	/** image resources */
	private FileResource stopImageFileResource = new FileResource(
			new File(basepath + "/WEB-INF/images/control-stop-square.png"));
	/** image resources */
	private FileResource restartImFileResource = new FileResource(
			new File(basepath + "/WEB-INF/images/arrow-repeat.png"));

	/** panel for buttons */
	private HorizontalLayout optionsPanel = new HorizontalLayout();

	/** panel for log related actions */
	private HorizontalLayout controlPanel = new HorizontalLayout();

	/** button for start service */
	private Button startButton = new Button("Start", startImageFileResource);

	/** button for stop service */
	private Button stopButton = new Button("Stop", stopImageFileResource);

	/** button for restart service */
	private Button restartButton = new Button("Restart", restartImFileResource);

	/** button for refresh crawler status */
	private Button refreshStatsButton = new Button("Refresh Crawler Status");

	/** wrapper for status stuff */
	private HorizontalLayout statusPanel = new HorizontalLayout();

	/** label for display status */
	private Label statsLabel = new Label("");

	/** area for display feedback message */
	private TextArea displayArea = new TextArea();

	/** log path settings */
	private Button setLogButton = new Button("Set Log Path");

	/** log path text box */
	private TextField logPathField = new TextField();

	/** label for feedback messages */
	private Label messageLabel = new Label();

	/** wrapper layout for log related part */
	private AbsoluteLayout wrapper = new AbsoluteLayout();

	/** the 7 days performance chart */
	private Component dayChart;

	/** the 24 hours performance chart */
	private Component hourChart;

	/** wrapper for the bottom part of page */
	private VerticalLayout dayChartWrapper = new VerticalLayout();

	/** wrapper for daily chart setting components */
	private HorizontalLayout chartDailySettingsWrapper = new HorizontalLayout();
	
	/** wrapper for hourly chart setting components */
	private HorizontalLayout chartHourlySettingsWrapper = new HorizontalLayout();
	
	/** wrapper contains the chart */
	private VerticalLayout chartWrapper = new VerticalLayout();

	/** end point date for the performace chart */
	private Date endDate = new Date(System.currentTimeMillis());

	// use this endDate if there is no recent data
	// private Date endDate = new Date(System.currentTimeMillis()-24*300*3600*1000L);

	/** prev button for daily performance plot */
	private Button prevDays = new Button("Prev");

	/** next button for daily performance plot */
	private Button nextDays = new Button("Next");
	
	/** prev button for hourly performance plot */
	private Button nextHours = new Button("Next");
	
	/** prev button for hourly performance plot */
	private Button prevHours = new Button("Prev");

	/** wrapper tabsheet */
	private TabSheet tabSheet = new TabSheet();

	/** wrapper for log under tab sheet */
	private VerticalLayout logTab = new VerticalLayout();

	/** wrapper for daily chart under tab sheet */
	private VerticalLayout dayChartTab = new VerticalLayout();

	/** wrapper for hourly chart under tab sheet */
	private VerticalLayout hourChartTab = new VerticalLayout();

	/** wrapper for log stuff */
	private HorizontalLayout logStuffPanel = new HorizontalLayout();

	/**
	 * constructor
	 */
	public CrawlerControl() {
		setSizeFull();
		messageLabel.setCaptionAsHtml(true);
		statsLabel.setCaptionAsHtml(true);
		Properties p = new Properties();
		try {
			File configFile = new File(basepath + "/WEB-INF/configurations.properties");
			FileInputStream configFileInputStream = new FileInputStream(configFile);
			p.load(configFileInputStream);
			logPathField.setValue(p.getProperty("LOGURL", "/home/zmz0305/log"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// actions for clicking buttons
		startButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Executor startExecutor = new Executor(Commands.startCommand(), ".", false);
				try {
					String msg = startExecutor.executeCommand();
					Notification.show(msg, Type.ERROR_MESSAGE);
					Notification.show("Start Crawler", msg, com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION);
				} catch (IOException | InterruptedException e) {
					Notification.show("Error", e.toString(), Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		stopButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Executor stopExecutor = new Executor(Commands.stopCommand(), ".", false);
				try {
					String msg = stopExecutor.executeCommand();
					Notification.show(msg, Type.ERROR_MESSAGE);
					messageLabel.setCaption("<p style=\"color:red\">" + msg + "</p>");
					Notification.show("Start Crawler", msg, com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION);
				} catch (IOException | InterruptedException e) {
					Notification.show("Error", e.toString(), Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		restartButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Executor restartExecutor = new Executor(Commands.restartCommand(), ".", false);
				try {
					String msg = restartExecutor.executeCommand();
					Notification.show(msg, Type.ERROR_MESSAGE);
					messageLabel.setCaption("<p style=\"color:red\">" + msg + "</p>");
					Notification.show("Start Crawler", msg, com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION);
				} catch (IOException | InterruptedException e) {
					Notification.show("Error", e.toString(), Type.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}
		});

		setLogButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				config.setLogPath(logPathField.getValue());
				p.setProperty("LOGURL", logPathField.getValue());
				try {
					p.store(new FileWriter(new File(basepath + "/WEB-INF/configurations.properties")), "log file path");
				} catch (IOException e) {
					e.printStackTrace();
				}
				refreshLog();
			}
		});

		setLogButton.setClickShortcut(KeyCode.ENTER);
		TimerTask statusRefreshTask = new TimerTask() {
			@Override
			public void run() {
				refreshStatus();
			}
		};
		Timer timer = new Timer();
		timer.schedule(statusRefreshTask, 0, 10000);

		refreshStatsButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				refreshStatus();
			}
		});
		logStuffPanel.addComponent(setLogButton);
		logStuffPanel.addComponent(logPathField);
		logStuffPanel.setSpacing(true);
		logTab.addComponent(logStuffPanel);
		logTab.addComponent(displayArea);
		logTab.setExpandRatio(displayArea, 1);
		logTab.setSizeFull();
		tabSheet.addTab(logTab, "Control & Log");
		tabSheet.addTab(dayChartTab, "Daily Chart");
		tabSheet.addTab(hourChartTab, "Hourly Chart");
		tabSheet.setSizeFull();
		// configuare panels
		statusPanel.addComponent(statsLabel);
		statsLabel.addStyleName("crawlerstatus-label");
		statsLabel.setSizeFull();
		statusPanel.setComponentAlignment(statsLabel, Alignment.MIDDLE_CENTER);
		logPathField.setWidth("500px");
		logPathField.setInputPrompt("log file path...");
		controlPanel.addComponent(startButton);
		controlPanel.addComponent(stopButton);
		controlPanel.addComponent(restartButton);
		controlPanel.addComponent(statsLabel);
		wrapper.addComponent(controlPanel, "left: 0px; top: 1px");
		wrapper.addComponent(statusPanel, "left: 0px; top: 35px");

		try {
			String msg = Executor.getLastNLogBytes(new File(config.getLogPath()), 30000);
			displayArea.setValue(msg);
		} catch (IOException e) {
			displayArea.setValue(e.toString());
		}
		displayArea.setSizeFull();

		addComponent(controlPanel);
		addComponent(tabSheet);
		setExpandRatio(tabSheet, 1);

		chartRelated();
	}

	/**
	 * wrapper function for chart related stuff
	 */
	private void chartRelated() {
		chartDailySettingsWrapper.addComponent(prevDays);
		chartDailySettingsWrapper.addComponent(nextDays);
		chartHourlySettingsWrapper.addComponent(prevHours);
		chartHourlySettingsWrapper.addComponent(nextHours);
		dayChartTab.addComponent(chartDailySettingsWrapper);
		hourChartTab.addComponent(chartHourlySettingsWrapper);
		dayChartTab.setSizeFull();
		hourChartTab.setSizeFull();
		
		prevDays.addClickListener(e -> {
			endDate = new Date(endDate.getTime() - 7 * 24 * 3600 * 1000L);
			refreshDayChart(endDate);
		});
		nextDays.addClickListener(e -> {
			endDate = new Date(endDate.getTime() + 7 * 24 * 3600 * 1000L);
			refreshDayChart(endDate);
		});

		nextHours.addClickListener(e -> {
			endDate = new Date(endDate.getTime() + 24 * 3600 * 1000L);
			refreshHourChart(endDate);
		});
		
		prevHours.addClickListener(e -> {
			endDate = new Date(endDate.getTime() - 24 * 3600 * 1000L);
			refreshHourChart(endDate);
		});
		
		refreshDayChart(endDate);
		refreshHourChart(endDate);
	}

	/**
	 * refresh the crawler status
	 */
	private void refreshStatus() {
		Executor statusExecutor = new Executor(Commands.statusCommand(), ".", false);
		try {
			// enable/disable buttons based on returned message
			String msg = statusExecutor.executeCommand();
			if (msg.contains("start/running")) {
				statsLabel.setCaption("<p style=\"color:green\">Crawler is running</p>");
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				restartButton.setEnabled(true);
			} else {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				restartButton.setEnabled(false);
				statsLabel.setCaption("<p style=\"color:red\">Crawler is NOT running</p>");
			}
		} catch (IOException | InterruptedException e) {
			statsLabel.setCaption(e.toString());
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			restartButton.setEnabled(false);

		}
	}

	/**
	 * refresh the 7 days chart
	 * 
	 * @param endDate
	 *            the end date, usually current date
	 **/
	private void refreshDayChart(Date endDate) {
		try {
			long endDateMil = endDate.getTime();
			Date temp = new Date(endDateMil+24*3600*1000l);
			temp.setHours(0);
			temp.setMinutes(0);
			temp.setSeconds(0);
			Integer[][] wholdDayData = new Integer[5][7];
			long tempEnd = temp.getTime();
			for (int i = 0; i < 7; i++) {
				wholdDayData[0][i] = new Integer(CrawlStatsReader.getPerformaceData(new Date(tempEnd - 24 * 60 * 60 * 1000L),
						new Date(tempEnd))[1]);
				wholdDayData[1][i] = new Integer(CrawlStatsReader.getPerformaceData(new Date(tempEnd - 24 * 60 * 60 * 1000L),
						new Date(tempEnd))[2]);
				wholdDayData[2][i] = new Integer(CrawlStatsReader.getPerformaceData(new Date(tempEnd - 24 * 60 * 60 * 1000L),
						new Date(tempEnd))[0]);
				wholdDayData[3][i] = new Integer(CrawlStatsReader.getPerformaceData(new Date(tempEnd - 24 * 60 * 60 * 1000L),
						new Date(tempEnd))[3]);
				wholdDayData[4][i] = new Integer(CrawlStatsReader.getPerformaceData(new Date(tempEnd - 24 * 60 * 60 * 1000L),
						new Date(tempEnd))[4]);
				tempEnd = tempEnd - 24 * 60 * 60 * 1000L;
			}
			if (dayChart != null) {
				dayChartTab.removeComponent(dayChart);
			}
			dayChart = StatsDayColumn.getChart(endDate, wholdDayData);
			dayChartTab.addComponent(dayChart);
			dayChartTab.setExpandRatio(dayChart, 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * refresh the hourly chart
	 * 
	 * @param endDate
	 *            the end date, usually current date
	 */
	private void refreshHourChart(Date endDate) {
		try {
			long endDateMil = endDate.getTime();
			Date temp = new Date(endDateMil+3600*1000l);
			temp.setMinutes(0);
			temp.setSeconds(0);
			Integer[][] data = new Integer[5][24];
			long tempEnd = endDateMil;
			for (int i = 0; i < 24; i++) {
				data[0][i] = new Integer(
						CrawlStatsReader.getPerformaceData(new Date(tempEnd - 3600 * 1000l), new Date(tempEnd))[1]);
				data[1][i] = new Integer(
						CrawlStatsReader.getPerformaceData(new Date(tempEnd - 3600 * 1000l), new Date(tempEnd))[2]);
				data[2][i] = new Integer(
						CrawlStatsReader.getPerformaceData(new Date(tempEnd - 3600 * 1000l), new Date(tempEnd))[0]);
				data[3][i] = new Integer(
						CrawlStatsReader.getPerformaceData(new Date(tempEnd - 3600 * 1000l), new Date(tempEnd))[3]);
				data[4][i] = new Integer(
						CrawlStatsReader.getPerformaceData(new Date(tempEnd - 3600 * 1000l), new Date(tempEnd))[4]);
				tempEnd = tempEnd - 3600 * 1000l;
			}
			if (hourChart != null) {
				hourChartTab.removeComponent(hourChart);
			}
			hourChart = Stats24HColumn.getChart(endDate, data);
			hourChartTab.addComponent(hourChart);
			hourChartTab.setExpandRatio(hourChart, 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * read log file with the newest log path in config
	 */
	public void refreshLog() {
		try {
			String msg = Executor.getLastNLogBytes(new File(config.getLogPath()), 30000);
			displayArea.setValue(msg);
		} catch (IOException e) {
			displayArea.setValue(e.toString());
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
