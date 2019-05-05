package runnables;

import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.fazecast.jSerialComm.SerialPort;

import main.GlobalFunctions;
import main.GlobalVariables;
import object_classes.Node;
import tools.DebugTool;
import tools.RRDTool;

/**
 * The Serial Read Thread receives Serial Messages from the Nodes, and handles them.
 * The messages received are written complying the Serial Communication JSON standard.
 * @author Felix
 */
public class SerialReadThread implements Runnable {
	
	
	// Initialize serial port with null
	SerialPort serialPort = null;
	

	/**
	 * @param serialPort
	 * Set Serial Port that should be used for communicating with Nodes.
	 */
	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}
	
	
	@Override
	public void run() {
		
		// if Serial Port is not already set -> throw an exception
		if (serialPort == null) {
			throw new RuntimeException("Serial Port was null!");
		}
		serialPort.openPort();
		
		// Reuse received string 
		String received = "";
		
		// Handle serial messages as long as the program is running
		while (GlobalVariables.RUNNING) {

			try {

				// Poll for incoming messages every X ms
				while (serialPort.bytesAvailable() == 0 || serialPort.bytesAvailable() < 0) {
					Thread.sleep(GlobalVariables.SERIAL_MESSAGE_POLL_TIME_MS);
				}

				// Read Serial message into Buffer
				byte[] readBuffer = new byte[serialPort.bytesAvailable()];
				serialPort.readBytes(readBuffer, readBuffer.length);
				received = received + new String(readBuffer, StandardCharsets.UTF_8);

				// if serial Message ends with a newline the "sentence" is complete
				if (received.endsWith("\n")) {

					// Modify received text
					received = received.replaceAll("\r", "");
					received = received.replaceAll("\n", "");

					// Split into messages into jsonObjects
					String[] messages = received.split("\\}");

					// Handle all messages (most of the time only one at a time is received)
					handleMessages(messages);

					// reset received text
					received = "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	
	/**
	 * Handle every received serial message stored in an array.
	 * @param messages array of serial messages.
	 */
	private void handleMessages(String[] messages) {
		for (String message : messages) {
			
			// Ignore empty messages and prepare JSON Format
			if (message == null || message.equals("") || message.length() < 5) continue;
			message = message + "}"; 	// After splitting message by "}" the "}" has to be added
			
			// Print Serial Message to debug text
			DebugTool.debug_print(SerialReadThread.class, "Read: " + message);
			
			// Parse String to JSON
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) JSONValue.parseWithException(message);
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
			
			// if message was successfully converted to a JSON Object
			if (jsonObject != null) {
				
				// Get id of the plant measure station
				String id = (String) jsonObject.get("id");
				
				// Check Node List if the Node already exists
				Node node = GlobalFunctions.getNodeById(id);
				
				// Node does not exist yet. Add it to the List.
				if (node == null) {
					DebugTool.debug_print(SerialReadThread.class, "New Node with id " + id + " detected.");
					
					// Add Node to List and create Database for it
					node = new Node(id);
					GlobalVariables.nodeList.add(node);
				}
				
				// Write values to database and break the for loop
				RRDTool.createDatabaseIfNotExist(id);
				RRDTool.writeData(jsonObject, node);
			}
		}
	}
}