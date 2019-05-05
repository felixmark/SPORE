package main;

import object_classes.Node;

/**
 * This class contains all functions that can be used everywhere.
 */
public class GlobalFunctions {

	//======================== General Functions ========================
	public static void delay(int milliseconds) {
		try { Thread.sleep(milliseconds); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	


	/**
	 * Finds a Node with the given id in the Node Array.
	 * @param id ID of the Node to find
	 * @return Node with given id
	 */
	public static Node getNodeById(String id) {
		if (id == null) return null;
		for (Node n : GlobalVariables.nodeList) {
			if (n.getId().equals(id)) {
				// Node found!
				return n;
			}
		}
		return null;
	}
}
