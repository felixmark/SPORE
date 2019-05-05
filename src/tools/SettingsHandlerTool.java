package tools;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import main.GlobalVariables;

public class SettingsHandlerTool {
	// Create comments with ALT + Shift + J
	
	/*
	 * Settings example:
	 * {
	 * 	"mail_sender"	:"...",
	 * 	"mail_receiver"	:"...",
	 * 	"pass_sender"	:"...",
	 * 	"plot_method"	:"..."
	 * }
	 * */

	public static String JSON_SETTINGS_NAME_RECEIVER = "name_receiver";
	public static String JSON_SETTINGS_MAIL_RECEIVER = "mail_receiver";
	public static String JSON_SETTINGS_MAIL_SENDER = "mail_sender";
	public static String JSON_SETTINGS_PASSWORD_SENDER = "pass_sender";
	public static String JSON_SETTINGS_PLOT_METHOD = "plot_method";
	public static String JSON_SETTINGS_BACKLOG_FROM = "backlog_from";
	public static String JSON_SETTINGS_BACKLOG_TO = "backlog_to";
	
	
	/**
	 * @param jsonObject The JSON Object to store in the settings file.
	 */
	public static void saveSettingsToFile() {
		FileWriterTool.writeFile(
			GlobalVariables.FOLDER_DATA, 
			GlobalVariables.FILENAME_SETTINGS, 
			GlobalVariables.settings.toJSONString()
		);
	}
	
	
	/**
	 * Loads the Settings from the settings file and returns them as a JSON Object.
	 * @return JSON Object containing the settings or null if File does not exist.
	 */
	public static JSONObject loadSettingsFromFile() {
		File f = new File(GlobalVariables.FOLDER_DATA + GlobalVariables.FILENAME_SETTINGS);
        if (f.exists()){
        	try {
            	JSONParser jp = new JSONParser();
				Object o = jp.parse(new FileReader(GlobalVariables.FOLDER_DATA + GlobalVariables.FILENAME_SETTINGS));
				if (o == null) return null;
				
				// Set global Variables
				if (((JSONObject) o).get(JSON_SETTINGS_BACKLOG_FROM) != null && ((JSONObject) o).get(JSON_SETTINGS_BACKLOG_TO) != null) {
					GlobalVariables.BACKLOGTIME_FROM = (long) ((JSONObject) o).get(JSON_SETTINGS_BACKLOG_FROM);
					GlobalVariables.BACKLOGTIME_TO = (long) ((JSONObject) o).get(JSON_SETTINGS_BACKLOG_TO);
				}
				
				return (JSONObject) o;
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return null;
	}
	

	//======================== SETTINGS INFORMATION GETTER ========================
	
	public static String getSenderMailFromSettings() {
		return (String) GlobalVariables.settings.get(JSON_SETTINGS_MAIL_SENDER);
	}
	public static String getReceiverMail() {
		return (String) GlobalVariables.settings.get(JSON_SETTINGS_MAIL_RECEIVER);
	}
	public static String getSenderPasswordFromSettings() {
		return (String) GlobalVariables.settings.get(JSON_SETTINGS_PASSWORD_SENDER);
	}
	public static String getPlotMethodFromSettings() {
		return (String) GlobalVariables.settings.get(JSON_SETTINGS_PLOT_METHOD);
	}
	public static String getReceiverName() {
		return (String) GlobalVariables.settings.get(JSON_SETTINGS_NAME_RECEIVER);
	}
	public static String getBacklogtimeFrom() {
		return (String) GlobalVariables.settings.get(JSON_SETTINGS_BACKLOG_FROM);
	}
	public static String getBacklogtimeTo() {
		return (String) GlobalVariables.settings.get(JSON_SETTINGS_BACKLOG_TO);
	}
}
