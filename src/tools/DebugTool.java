package tools;

import main.GlobalVariables;

public class DebugTool {
	
	// ====================== GENERAL FUNCTIONS ======================
	
	/**
	 * Print text to the console if Program runs in DEBUG mode.
	 * @param someClass Calling class
	 * @param message Text to print out to the console
	 */
	public static void debug_print(Class<?> someClass, String message) {
		// Only print if running in DEBUG mode
		if (GlobalVariables.DEBUG) { always_print(someClass, message); }
	}


	/**
	 * Print text to the console in the form of: [  ClassName  ] Message and store message in the logfileTexts.
	 * @param someClass Calling Class
	 * @param message Message to print out
	 */
	public static void always_print(Class<?> someClass, String message) {
		String className = someClass.getName().substring(someClass.getName().lastIndexOf('.')+1);
		int len = className.length();
		int left = (GlobalVariables.MAX_CLASS_NAME_LENGTH - len)/2;
		int right = (GlobalVariables.MAX_CLASS_NAME_LENGTH - len)-left;
		
		String text = "[";
		for (int i = 0; i < left; ++i) text = text + " ";
		text = text + className;
		for (int i = 0; i < right; ++i) text = text + " ";
		text = text + "] " + message;
		
		System.out.println(text);
		GlobalVariables.logfileTexts.add(text);
	}
}
