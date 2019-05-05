package tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import main.GlobalVariables;
import object_classes.Node;

public class TCPCommandHandlerTool {
	
	
	

	
	/**
	 * Executes the given TCP Command with the passed data.
	 * @param command The command to execute
	 * @param data The data for the command
	 * @return String that will be sent back to the sender
	 */
	public static String executeCommand(String command, String data) {
		if (command.equals(GlobalVariables.TCP_CMD_ADDSTATUS)) {
			return TCPCommandHandlerTool.addStatus();
		} else if (command.equals(GlobalVariables.TCP_CMD_GET_NODES)) {
			return TCPCommandHandlerTool.getNodes();
		} else if (command.equals(GlobalVariables.TCP_CMD_PUSH_NODE_SETTINGS)) {
			return TCPCommandHandlerTool.pushNodeSettings(data);
		} else if (command.equals(GlobalVariables.TCP_CMD_PUSH_SETTINGS)) {
			return TCPCommandHandlerTool.pushSettings(data);
		} else if (command.equals(GlobalVariables.TCP_CMD_GET_SETTINGS)) {
			return TCPCommandHandlerTool.getSettings();
		} else if (command.equals(GlobalVariables.TCP_CMD_DELETE_NODE)) {
			return TCPCommandHandlerTool.removeNode(data);
		} else if (command.equals(GlobalVariables.TCP_CMD_PUSH_BACKLOG_TIME)) {
			return TCPCommandHandlerTool.pushBacklogTime(data);
		} else if (command.equals(GlobalVariables.TCP_CMD_GET_BACKLOG_TIME)) {
			return TCPCommandHandlerTool.getBacklogTime();
		}
		return GlobalVariables.TCP_STANDARD_FAIL;
	}
	
	
	/**
	 * Check if a new node was found on SERIAL Port in the last 3 seconds
	 * @return Success/Fail
	 */
	public static String addStatus() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -3);
		for (Node plant : GlobalVariables.nodeList) {
			if (plant.getCreationTime().after(calendar.getTime())) {
				// Added node was found! -> respond with node id
				return plant.getId();
			}
		}
		return GlobalVariables.TCP_STANDARD_FAIL;
	}
	
	
	/**
	 * Get all Nodes in ArrayList and send back.
	 * @return All Nodes as a JSONString
	 */
	@SuppressWarnings("unchecked")
	public static String getNodes() {
		JSONArray jA = new JSONArray();
		for (Node p : GlobalVariables.nodeList) {
			jA.add(JSONValue.parse(p.toString()));
		}
		return jA.toJSONString();
	}
	
	
	/**
	 * @param data has to be a JSONObject String.
	 * Push the Settings of one Node to the Node Array.
	 * @return Success/Fail
	 */
	public static String pushNodeSettings(String data) {
		JSONObject jO = (JSONObject) JSONValue.parse(data.replace("\\\"","\""));
		for (Node n : GlobalVariables.nodeList) {
			if (n.getId().equals(jO.get("id"))) {
				// Set Values of the right Node
				n.setValues(jO);
				GoogleChartsCreatorTool.createGoogleChartsJSON();
				NodeHandlerTool.writeNodesToFile();
				return GlobalVariables.TCP_STANDARD_SUCCESS;
			}
		}
		return GlobalVariables.TCP_STANDARD_FAIL;
	}
	
	
	/**
	 * @param id The ID of the Node to be removed
	 * Removes one Node from the NodeList.
	 * @return Success/Fail
	 */
	public static String removeNode(String id) {

		for (Node n : GlobalVariables.nodeList) {
			if (n.getId().equals(id)) {
				GlobalVariables.nodeList.remove(n);
				GoogleChartsCreatorTool.createGoogleChartsJSON();
				NodeHandlerTool.writeNodesToFile();
				return GlobalVariables.TCP_STANDARD_SUCCESS;
			}
		}
		return GlobalVariables.TCP_STANDARD_FAIL;
	}
	
	
	/**
	 * @param data JSONObject of the Settings as a String
	 * Push Settings to settings file.
	 * @return Success/Fail
	 */
	public static String pushSettings(String data) {
		JSONObject jO = (JSONObject) JSONValue.parse(data);
		if (jO != null) {
			// Text was successfully parse to a JSON Object
			GlobalVariables.settings = jO;
			SettingsHandlerTool.saveSettingsToFile();
			return GlobalVariables.TCP_STANDARD_SUCCESS;
		} else {
			return GlobalVariables.TCP_STANDARD_FAIL;
		}
	}
	
	
	/**
	 * Load Settings from settings file.
	 * @return Success/Fail
	 */
	public static String getSettings() {
		JSONObject jO = GlobalVariables.settings;
		if (jO != null) {
			return jO.toJSONString().trim();
		} else {
			return GlobalVariables.TCP_STANDARD_FAIL;
		}
	}


	/**
	 * Push backlogtime and create new JSON of data.
	 * @param data String in the format of 2018-11-15#2018-11-16
	 * @return "OK" or "NO"
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String pushBacklogTime(String data) {
		if (!data.contains("#")) return GlobalVariables.TCP_STANDARD_FAIL;
		String [] daten = data.split("#");
		if (daten.length < 2) return GlobalVariables.TCP_STANDARD_FAIL;
		
		Date von, bis;
		long sekunden_von = 1, sekunden_bis = 0;
		try {
			von = new SimpleDateFormat("yyyy-MM-dd").parse(daten[0]);
			bis = new SimpleDateFormat("yyyy-MM-dd").parse(daten[1]);
			bis.setHours(23);
			bis.setMinutes(55);
			bis.setSeconds(0);
			
			sekunden_von = ((new Date()).getTime() - von.getTime())/1000;
			sekunden_bis = ((new Date()).getTime() - bis.getTime())/1000;
		} catch (ParseException e) { e.printStackTrace(); }
		
		GlobalVariables.BACKLOGTIME_FROM = sekunden_von;
		GlobalVariables.BACKLOGTIME_TO = sekunden_bis;
		
		// Save values to Settings
		GlobalVariables.settings.put(SettingsHandlerTool.JSON_SETTINGS_BACKLOG_FROM, GlobalVariables.BACKLOGTIME_FROM);
		GlobalVariables.settings.put(SettingsHandlerTool.JSON_SETTINGS_BACKLOG_TO, GlobalVariables.BACKLOGTIME_TO);
		SettingsHandlerTool.saveSettingsToFile();
		
		DebugTool.debug_print(TCPCommandHandlerTool.class, "Backlogtime set to "+data+".");
		GoogleChartsCreatorTool.createGoogleChartsJSON();
		return GlobalVariables.TCP_STANDARD_SUCCESS;
	}
	
	
	/**
	 * Get Backlogtimes in yyyy-MM-dd format.
	 * @return String according to format
	 */
	public static String getBacklogTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar from = Calendar.getInstance();
		from.add(Calendar.SECOND, (int) -GlobalVariables.BACKLOGTIME_FROM);
		Calendar to = Calendar.getInstance();
		to.add(Calendar.SECOND, (int) -GlobalVariables.BACKLOGTIME_TO);
		return sdf.format(from.getTimeInMillis()) + "#" + sdf.format(to.getTimeInMillis());
	}
}
