package tools;

import com.fazecast.jSerialComm.SerialPort;

import main.GlobalVariables;
import runnables.SerialReadThread;

public class SerialTool {
	
	/**
	 * Initialize the given serial port.
	 * @param portName Name of the port to connect to
	 * @param baudRate Baud Rate of the serial port
	 * @param numDatabits Number of data bits
	 * @param numStopbits Number of stop bits
	 */
	public static void init(String portName, int baudRate, int numDatabits, int numStopbits) {
		
		// As long as there are no connected serial ports and the application is running
		while (GlobalVariables.RUNNING) {
			
			// Get list of all Serial ports
			SerialPort ports[] = SerialPort.getCommPorts();
			
			if (ports.length == 0) {

				// No serial ports found
				if (!GlobalVariables.SERIAL_MUTED) DebugTool.debug_print(SerialTool.class, "No Serial Ports found!");
				try {
					Thread.sleep(GlobalVariables.SERIAL_POLL_TIMEOUT);
				} catch (InterruptedException e) { /* Ignore sleep problem. */ }

			} else {

				// Search for the desired Serial port
				for (SerialPort port : ports) {
					DebugTool.debug_print(SerialTool.class, "Found Serial Port: " + port.getSystemPortName());

					if (portName.equals(port.getSystemPortName()) || portName.equals("?")) {

						// Set Serial parameters
						port.setBaudRate(baudRate);
						port.setNumDataBits(numDatabits);
						port.setNumStopBits(numStopbits);

						// Create and start Serial Reader Thread
						SerialReadThread serialreader = new SerialReadThread();
						serialreader.setSerialPort(port);
						Thread thread = new Thread(serialreader);
						thread.start();
					}
				}
				
				// Serial connection found. Exit the initialization.
				return;
			}
		}
	}
}
