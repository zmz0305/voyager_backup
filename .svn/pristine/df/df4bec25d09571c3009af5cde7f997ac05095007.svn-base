package edu.illinois.ncsa.cline.voyager;

import java.util.ArrayList;

/**
 * a lib class that contains commonly used commands
 * 
 * @author zmz0305
 *
 */
public class Commands {
    /** command to start crawler */
	private static ArrayList<String> startCommand = new ArrayList<String>();
	
	/** command to stop crawler */
	private static ArrayList<String> stopCommand = new ArrayList<String>();
	
	/** command to restart crawler */
	private static ArrayList<String> restartCommand = new ArrayList<String>();
	
	/** custom command for testing */
	private static ArrayList<String> testCommand = new ArrayList<String>();
	
	/** command to see crawler status */
	private static ArrayList<String> statusCommand = new ArrayList<String>();
	
	/**
	 * command for start crawl service
	 * @return commands
	 */
	public static ArrayList<String> startCommand() {
		if (startCommand.size() == 0) {
			startCommand.add("start");
			startCommand.add("crawl");
			return startCommand;
		} else {
			return startCommand;
		}
	}

	/**
	 * command for stop crawl service
	 * @return commands
	 */
	public static ArrayList<String> stopCommand() {
		if (stopCommand.size() == 0) {
			stopCommand.add("stop");
			stopCommand.add("crawl");
			return stopCommand;
		} else {
			return stopCommand;
		}
	}
	
	/**
	 * command for restart crawl service
	 * @return commands
	 */
	public static ArrayList<String> restartCommand() {
		if (restartCommand.size() == 0) {
			restartCommand.add("restart");
			restartCommand.add("crawl");
			return restartCommand;
		} else {
			return restartCommand;
		}
	}
	
	/**
	 * command for getting status
	 * @return status command
	 */
	public static ArrayList<String> statusCommand() {
	    if(statusCommand.size() == 0) {
	        statusCommand.add("status");
	        statusCommand.add("crawl");
	        return statusCommand;
	    } else {
	        return statusCommand;
	    }
	}
	
	/**
	 * just for testing
	 * @return testcommand
	 */
	public static ArrayList<String> testCommand() {
		if (testCommand.size() == 0) {
			testCommand.add("ls");
			return testCommand;
		} else {
			return testCommand;
		}
	}
}
