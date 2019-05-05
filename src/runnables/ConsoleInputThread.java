package runnables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import main.GlobalFunctions;
import main.GlobalVariables;
import main.GlobalVariables.WARNING_TYPE;
import object_classes.Node;
import tools.DebugTool;
import tools.NodeHandlerTool;
import tools.SettingsHandlerTool;

public class ConsoleInputThread implements Runnable {
	
	@Override
	public void run() {
		
		// Reuse input string 
		String input;
		Scanner scan = new Scanner(System.in);
		printHelp();
		
		// Handle serial messages as long as the program is running
		while (GlobalVariables.RUNNING) {
			try {
				input = scan.nextLine();
				handleConsoleInput(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		scan.close();
	}

	
	/**
	 * Handle every console input.
	 * @param input The input string to process.
	 */
	private void handleConsoleInput(String input) {
		String [] split = input.split(" ");
		
		if (input.equals("nodes")) {
			for (Node n : GlobalVariables.nodeList) {
				System.out.println("Node id: "+n.getId()+", Node name: "+n.getName());
			}
		} else if (input.equals("clearwarnings")) {
			for (Node n : GlobalVariables.nodeList) {
				n.clearWarnings();
			}
		} else if (input.equals("addwarnings")) {
			for (Node n : GlobalVariables.nodeList) {
				ArrayList<WARNING_TYPE> someWarnings = new ArrayList<WARNING_TYPE>();
				someWarnings.add(WARNING_TYPE.TOO_DRY_BOT);
				someWarnings.add(WARNING_TYPE.TOO_DRY_TOP);
				n.setWarnings(someWarnings);
				n.setMin_daily_sun(10);
				n.addSunshineValue(2);
			}
		} else if (input.equals("serialmute")) {
			GlobalVariables.SERIAL_MUTED = true;
		} else if (input.equals("help")) {
			printHelp();
		} else if (input.equals("sendmail")) {
			TimedExecutorThread.everyHourFunction(true);
		} else if (input.equals("quit")) {
			try {
				TCPServerThread.welcomeSocket.close();
			} catch (IOException e) { e.printStackTrace(); }
			DebugTool.always_print(ConsoleInputThread.class, "Shutting down program...");
			NodeHandlerTool.writeNodesToFile();
			SettingsHandlerTool.saveSettingsToFile();
			TimedExecutorThread.everyHourFunction(false);
			GlobalVariables.RUNNING = false;
			try { Thread.sleep(1000); } catch (Exception no) {}
			System.exit(0);
			return;
		} else if (input.startsWith("node")) {
			if (split.length > 1) {
				String id = split[1].trim();
				Node n = GlobalFunctions.getNodeById(id);
				if (n != null) {
					printNodeInfo(n);
				} else {
					if (id.equals("*")) {
						for (Node node : GlobalVariables.nodeList) {
							printNodeInfo(node);
							System.out.println("");
						}
					} else {
						System.out.println("Node not found!");
					}
				}
			}
		} else {
			System.out.println("Command not found. Try the   help   command.");
		}
	}
	
	/**
	 * Prints the Help menu.
	 */
	public static void printHelp() {
		DebugTool.always_print(ConsoleInputThread.class, "Commands:");
		DebugTool.always_print(ConsoleInputThread.class, "help \t \t Print help");
		DebugTool.always_print(ConsoleInputThread.class, "serialmute \t \t Mute \"No Serial Ports found!\" warning");
		DebugTool.always_print(ConsoleInputThread.class, "sendmail \t \t Force datalog with email notifications");
		DebugTool.always_print(ConsoleInputThread.class, "nodes \t \t Print ids of all nodes");
		DebugTool.always_print(ConsoleInputThread.class, "node [id] \t \t Print information of a node (* for all nodes)");
		DebugTool.always_print(ConsoleInputThread.class, "clearwarnings \t Clear all Node warnings");
		DebugTool.always_print(ConsoleInputThread.class, "addwarnings \t \t Add some warnings to all nodes");
		DebugTool.always_print(ConsoleInputThread.class, "quit \t \t Stop the application");
	}

	/**
	 * @param node Node which info should be shown.
	 */
	public static void printNodeInfo(Node node) {
		System.out.println("Information:");
		System.out.println("Name: "+node.getName());
		System.out.println("Creation time: "+node.getCreationTime());
		System.out.println("Average sun: "+node.getAverageSun());
		System.out.println("Warnings: "+node.getWarnings());
	}
}