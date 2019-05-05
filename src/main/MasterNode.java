package main;

import java.util.Date;

import runnables.ConsoleInputThread;
import runnables.TCPServerThread;
import runnables.TimedExecutorThread;
import tools.DebugTool;
import tools.FolderHandlerTool;
import tools.NodeHandlerTool;
import tools.SerialTool;
import tools.SettingsHandlerTool;

/**
 * The MasterNode contains the main function of the application.
 */
public class MasterNode {
	
	//======================== MAIN ========================
	public static void main(String [] args) {
		
		// Get Parameters of program call
		handleProgramParameters(args);
		
		// Print Server Initialization
		printInitializationInfo();
		
		// Create Folder structure
		FolderHandlerTool.setJSONOutputFolder();
		FolderHandlerTool.createFolderStructure();
		GlobalFunctions.delay(GlobalVariables.DELAY_TASK);

		// Loading Nodes
		NodeHandlerTool.loadNodesFromFile();
		GlobalFunctions.delay(GlobalVariables.DELAY_TASK);
		
		// Starting TCP Server Thread
		Thread udpServerThread = new Thread(new TCPServerThread());
		udpServerThread.start();
		GlobalFunctions.delay(GlobalVariables.DELAY_TASK);
		
		// Start Data-log Thread
		Thread dataLogThread = new Thread(new TimedExecutorThread());
		dataLogThread.start();
		GlobalFunctions.delay(GlobalVariables.DELAY_TASK);

		// Start console input Thread
		Thread consoleInputThread = new Thread(new ConsoleInputThread());
		consoleInputThread.start();
		GlobalFunctions.delay(GlobalVariables.DELAY_TASK);
		
		// Load Settings from Settings File
		GlobalVariables.settings = SettingsHandlerTool.loadSettingsFromFile();
		if (GlobalVariables.settings != null) DebugTool.debug_print(MasterNode.class, "Settings: " + GlobalVariables.settings.toJSONString());
		GlobalFunctions.delay(GlobalVariables.DELAY_TASK);
		
		// Serial connect
		SerialTool.init("?", GlobalVariables.SERIAL_BAUD_RATE, GlobalVariables.SERIAL_DATA_BITS, GlobalVariables.SERIAL_STOP_BITS);
	}
	
	
	/**
	 * Print the initialization text.
	 */
	private static void printInitializationInfo() {
		DebugTool.always_print(MasterNode.class, "=================== "+GlobalVariables.APPNAME+" Server "+GlobalVariables.VERSION+" ===================");
		DebugTool.always_print(MasterNode.class, "Program parameters:");
		DebugTool.always_print(MasterNode.class, "-----------------------------------------------------");
		DebugTool.always_print(MasterNode.class, "-debug ..................... run server in debug mode");
		DebugTool.always_print(MasterNode.class, "=====================================================");
		DebugTool.always_print(MasterNode.class, "Server started at: "+GlobalVariables.sdfLong.format(new Date())+".");
		DebugTool.always_print(MasterNode.class, "Running in "+(GlobalVariables.DEBUG?"DEBUG":"SILENT")+" mode.");
	}


	/**
	 * Handle the given program parameters.
	 * @param args Contains program arguments
	 */
	public static void handleProgramParameters(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				
				// Debug Parameter
				if (arg.equals("-debug")) {
					GlobalVariables.DEBUG = true;
				}
			}
		}
	}
}
