package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import object_classes.Node;

/**
 * This class contains all variables that can be used everywhere.
 */
public class GlobalVariables {
	
	// Basic Definitions
	public static final String APPNAME = "SPORE";			// Name
	public static final String VERSION = "1.2";				// Server Version
	public static boolean DEBUG = false;					// for printing DEBUG Messages
	public static boolean RUNNING = true;					// run until set to false
	
	// Important Variables
	public static ArrayList<Node> nodeList = new ArrayList<Node>();
	public static JSONObject settings;
	
	// Other Definitions
	public static final int DELAY_TASK = 100;				// in ms
	public static final int DELAY_DATALOG = 50000;			// allow up to X ms delay when writing datalog (and poll only every X ms)
	public static long BACKLOGTIME_FROM = 60*60*24;			// -1d as default (changeable on runtime)
	public static long BACKLOGTIME_TO = 0;					// -0d as default
	public static final long FETCH_RESOLUTION = 60*10;		// Fetch every X min (60s * X)
	public static final int MAX_CLASS_NAME_LENGTH = 24;		// Maximum length of class names when printing them out.
	public static final ArrayList<String> logfileTexts = new ArrayList<String>();	// ArrayList of texts that should be stored in the logfile.
	public static final int TIME_SEND_MAIL_INT = 20;												// Send Mails every day at 20:00
	public final static String TIME_SEND_MAIL_STRING = String.valueOf(TIME_SEND_MAIL_INT)+":00";	// Don't touch.
	
	// SERIAL Definitions
	public static final long SERIAL_POLL_TIMEOUT = 2000;
	public static final int SERIAL_BAUD_RATE = 115200;		// Baud rate has to be the same on Nodes
	public static final int SERIAL_DATA_BITS = 8;
	public static final int SERIAL_STOP_BITS = 1;
	public static final long SERIAL_MESSAGE_POLL_TIME_MS = 100;	// Poll every X ms for new Serial messages
	public static boolean SERIAL_MUTED = false;					// Mute "No Serial Ports found!" Message
	
	// Filepath Definitions
	public static final String FOLDER_DATA = "data/";
	public static final String FOLDER_RRD = "rrd/";
	public static final String FILE_DATALOG = FOLDER_DATA + "datalog.txt";
	public static final String FILENAME_SETTINGS = "settings.json";
	public static final String FILENAME_JSON_EXPORT = "googlecharts.json";
	public static String FOLDER_JSON = "";					// Will be loaded on startup
	
	// TCP Definitions
	public static final int TCP_PORT = 14157;				// Port for communicating with PHP
	public static final int CLIENT_SOCKET_TIMEOUT = 5000;	// in ms
	public static final String TCP_STANDARD_FAIL = "no";	// for standardization purposes
	public static final String TCP_STANDARD_SUCCESS = "ok";	// for standardization purposes
	public static final String TCP_CMD_ADDSTATUS = "ADDSTATUS";	// User asked for add status
	public static final String TCP_CMD_GET_NODES = "GET_NODES";	// User asked for node list items
	public static final String TCP_CMD_PUSH_NODE_SETTINGS = "PUSH_NODE_SETTINGS"; // Push Node settings to List
	public static final String TCP_CMD_DELETE_NODE = "REMOVE_NODE"; 	// Removes Node from the node list
	public static final String TCP_CMD_PUSH_SETTINGS = "PUSH_SETTINGS"; // Push settings
	public static final String TCP_CMD_GET_SETTINGS = "GET_SETTINGS"; 	// Load settings
	public static final String TCP_CMD_PUSH_BACKLOG_TIME = "PUSH_BACKLOG_TIME";	// Push the new backlog time
	public static final String TCP_CMD_GET_BACKLOG_TIME = "GET_BACKLOG_TIME";	// Get the current backlog time
	
	// Email Definitions
	public enum WARNING_TYPE { TOO_DRY_TOP, TOO_DRY_CEN, TOO_DRY_BOT, TOO_MUCH_SUN, TOO_FEW_SUN };
	public static final String SMTP_HOST = "smtp.gmail.com";	// Host to send the warning mails
	public static final String SMTP_PORT = "587";				// Port of the host
	
	// Simple Date Formatter Definitions
	public static SimpleDateFormat sdfShort = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat sdfLong = new SimpleDateFormat("dd.MM.YYYY HH:mm");
}
