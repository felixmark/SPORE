package tools;

import java.io.File;
import java.util.ArrayList;

import main.GlobalVariables;

public class FolderHandlerTool {
	public static void createFolderStructure() {
		
		// Folders
		ArrayList<File> folders = new ArrayList<File>();
		folders.add(new File(GlobalVariables.FOLDER_DATA));
		folders.add(new File(GlobalVariables.FOLDER_RRD));
		folders.add(new File(GlobalVariables.FOLDER_JSON));

		// Check folders and create them if they do not exist
		for (File folder : folders) {
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
	}

	public static void setJSONOutputFolder() {
		
		// Set JSON Output folder (on Unix /var/www/json) 
		
		GlobalVariables.FOLDER_JSON = "";
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0) GlobalVariables.FOLDER_JSON = System.getenv("SystemDrive");
		GlobalVariables.FOLDER_JSON = GlobalVariables.FOLDER_JSON + "/";
		GlobalVariables.FOLDER_JSON = GlobalVariables.FOLDER_JSON + "var/www/json/";
		DebugTool.debug_print(FolderHandlerTool.class, "JSON export folder: "+GlobalVariables.FOLDER_JSON);
	}
}
