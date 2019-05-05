package runnables;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;

import main.GlobalFunctions;
import main.GlobalVariables;
import main.GlobalVariables.WARNING_TYPE;
import object_classes.Node;
import tools.DebugTool;
import tools.MailTool;
import tools.SettingsHandlerTool;


/**
 * Thread to write datalogs in the logfile.
 * This thread also triggers the Email notifications at a given time of the day.
 */
public class TimedExecutorThread implements Runnable {
	
	
	@Override
	public void run() {
		
		// Initial datalog
		everyHourFunction(false);
		
		// Create Date object holding the point of next datalog
		Date nextDataLog = new Date();
		Date currentDate = new Date();
		setDateToNextHour(nextDataLog);
		
		// Log data as long as the program is running
		while (GlobalVariables.RUNNING) {
			currentDate = new Date();
			if (currentDate.after(nextDataLog)) {
				everyHourFunction(false);
				setDateToNextHour(nextDataLog);
			}
			
			// Sleep for 50 seconds
			GlobalFunctions.delay(GlobalVariables.DELAY_DATALOG);
		}
	}

	
	/**
	 * This method writes the debug messages to the logfile, 
	 * and sends email notifications at a given time if there are any exceeded boundaries of plant measuring stations.
	 * @param sendMail Set if a Warning Mail should be forced.
	 */
	public static void everyHourFunction(boolean sendMail) {
		Date now = new Date();
		
		
		
		// ================================ Write datalog ================================
		
		DebugTool.debug_print(TimedExecutorThread.class,"Writing DATALOG. ("+GlobalVariables.sdfShort.format(now)+")");
		
		// Set target file
		String filename = GlobalVariables.FILE_DATALOG;
		File datalogFile = new File(filename);
		
		try {
			// Create file if it does not exist
			if (!datalogFile.exists()) datalogFile.createNewFile();
			
			// Write all debug lines to file
			while (!GlobalVariables.logfileTexts.isEmpty()) {
				Files.write(Paths.get(filename), (GlobalVariables.logfileTexts.get(0)+'\r'+'\n').getBytes(), StandardOpenOption.APPEND);
				GlobalVariables.logfileTexts.remove(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		

		// ================================ Send notification mails at a given time ================================
		
		if (sendMail || GlobalVariables.sdfShort.format(now).contains(GlobalVariables.TIME_SEND_MAIL_STRING)) {
			DebugTool.debug_print(TimedExecutorThread.class,"Sending warning mails if there are warnings..");
			
			for (Node n : GlobalVariables.nodeList) {
				ArrayList<WARNING_TYPE> warnings = n.getWarnings();
				if (warnings == null) warnings = new ArrayList<WARNING_TYPE>();
				if (n.getAverageSun() > -1 && n.getMin_daily_sun() > -1 && n.getMin_daily_sun() > n.getAverageSun()) warnings.add(WARNING_TYPE.TOO_FEW_SUN);
				if (n.getAverageSun() > -1 && n.getMax_daily_sun() > -1 && n.getMax_daily_sun() < n.getAverageSun()) warnings.add(WARNING_TYPE.TOO_MUCH_SUN);
				
				if (!warnings.isEmpty()) {
					// Send mail of the plant with warning(s)
					MailTool.sendWarningMail(warnings, n, SettingsHandlerTool.getReceiverName(), SettingsHandlerTool.getReceiverMail());
				}
				n.clearSunshineValues();
				n.clearWarnings();
			}
		}
	}
	
	
	
	/**
	 * Set a date to the next hour.
	 * @param date The date to be set to the next hour.
	 */
	@SuppressWarnings("deprecation")
	private static void setDateToNextHour(Date date) {
		date.setSeconds(0);
		date.setMinutes(0);
		date.setHours(date.getHours()+1);
	}
}
