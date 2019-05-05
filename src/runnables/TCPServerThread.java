package runnables;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import main.GlobalVariables;
import tools.DebugTool;
import tools.TCPCommandHandlerTool;


/**
 * The TCP Server Thread handles TCP messages.
 * It receives them, executes a given command with parameters, and sends a response to the sender.
 */
public class TCPServerThread implements Runnable {

	public static ServerSocket welcomeSocket = null;
	
	@Override
	public void run() {
		
		try {
			// Initialize reusable variables used in the thread
			welcomeSocket = new ServerSocket(GlobalVariables.TCP_PORT);
			String response = "";
            
			DebugTool.debug_print(TCPServerThread.class, "TCP Server started (Port: "+GlobalVariables.TCP_PORT+")");
            
            // Loop as long as the program is running
            while(GlobalVariables.RUNNING) {
            	try {
	            	// Init response
	            	response = null;
	            	
	            	// Wait for TCP connection
	            	Socket connectionSocket = welcomeSocket.accept();
	            	connectionSocket.setSoTimeout(GlobalVariables.CLIENT_SOCKET_TIMEOUT);
	            	
	    			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(),"UTF-8"));
	    			BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream(),"UTF-8"));
	    	
	    			String sentence = inFromClient.readLine().trim();
	    			
					// Debug print received TCP Command
					DebugTool.debug_print(TCPServerThread.class, "TCP COMMAND: " + sentence);
					
					// Split parsed Packet into COMMAND and DATA
					String command = sentence;
					String data = null;
					if (command.contains("#")) {
						command = command.substring(0,sentence.indexOf("#"));
						data = sentence.substring(sentence.indexOf("#")+1).trim();
					}
					
					// Execute COMMAND
					response = TCPCommandHandlerTool.executeCommand(command, data);
					
					// RESPOND TO CLIENT
					DebugTool.debug_print(TCPServerThread.class, "RESPONSE: "+response);
					outToClient.write(response+'\n');
					outToClient.flush();
					if (!connectionSocket.isClosed()) connectionSocket.close();
            	} catch (Exception o) {
            		if (welcomeSocket.isClosed()) return;
            		o.printStackTrace();
            	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { welcomeSocket.close(); } catch (IOException e) { e.printStackTrace(); }
		}
	}
}
