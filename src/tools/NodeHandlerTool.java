package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import main.GlobalVariables;
import object_classes.Node;

public class NodeHandlerTool {
	
	public static void writeNodesToFile() {
		// Write node list to Object Stream
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(GlobalVariables.FOLDER_DATA + "nodesave.data"));
			oos.writeObject(GlobalVariables.nodeList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { oos.close(); } catch (Exception e) { /* Object Output Stream could not be closed.. */ }
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadNodesFromFile() {
		// Load node list from Object Stream
		File nodesave = new File("data/nodesave.data");
		if (!nodesave.exists()) return;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(GlobalVariables.FOLDER_DATA + "nodesave.data"));
			GlobalVariables.nodeList = (ArrayList<Node>) ois.readObject();

			for (Node n : GlobalVariables.nodeList) {
				DebugTool.debug_print(NodeHandlerTool.class, "Loaded Node: "+n.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { ois.close(); } catch (Exception e) { e.printStackTrace(); }
		}
	}
}
