package tools;

import java.io.FileNotFoundException;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;

import main.GlobalVariables;
import object_classes.Node;

public class GoogleChartsCreatorTool {
	@SuppressWarnings("unchecked")
	public static void createGoogleChartsJSON() {
	    JSONObject jsonHolder = new JSONObject();
		JSONArray nodes = new JSONArray();
		JSONArray logtimes = new JSONArray();
		
	    for (Node javaNode : GlobalVariables.nodeList) {
	    	String nodeId = javaNode.getId();
	    	RrdDb rrdDatabase = null;
	    	
			try {
				rrdDatabase = new RrdDb(GlobalVariables.FOLDER_RRD+"nodedata_"+nodeId+".rrd");
				
			    // Fetch the data from the database
				Calendar calendar_from = Calendar.getInstance();
				calendar_from.add(Calendar.SECOND, (int) -GlobalVariables.BACKLOGTIME_FROM);
				Calendar calendar_to = Calendar.getInstance();
				calendar_to.add(Calendar.SECOND, (int) -GlobalVariables.BACKLOGTIME_TO);
				if (calendar_to.after(Calendar.getInstance())) calendar_to = Calendar.getInstance();
				
			    FetchRequest fetchRequest = rrdDatabase.createFetchRequest(ConsolFun.AVERAGE, calendar_from.getTimeInMillis()/1000, calendar_to.getTimeInMillis()/1000, GlobalVariables.FETCH_RESOLUTION);
			    FetchData fetchData;
				fetchData = fetchRequest.fetchData();
				
				// Fetch data source names from fetchdata
				String[] dataSourceNames = fetchData.getDsNames();
				
				// Create JSON Object for Node
		    	JSONObject node = new JSONObject();
		    	
		    	// Write text values to JSON Object
		    	node.putIfAbsent("id", nodeId);
		    	if (javaNode.getName() != null) node.putIfAbsent("name", javaNode.getName());
		    	else node.putIfAbsent("name", javaNode.getId());
		    	node.putIfAbsent("status", "Very good.");
		    	
		    	if (logtimes.isEmpty()) {
		    		long timestamps[] = fetchData.getTimestamps();
		    		long timeStamp = 0;
		    		for (int timeIndex = 0; timeIndex < timestamps.length; ++timeIndex) {
		    			timeStamp = timestamps[timeIndex];
		    			logtimes.add(timeStamp*1000);
		    		}
		    	}
		    	
		    	// Add all DOUBLE VALUE Properties (Humidity_top, Temperature_top,...)
			    for (int index = 0; index < dataSourceNames.length; ++index) {
			    	JSONArray valueList = new JSONArray();
			    	double values[][] = fetchData.getValues();
			    	for (int value_index = 0; value_index < values[index].length; ++value_index) {
			    		if (value_index == fetchData.getValues()[index].length-1 && (int)(fetchData.getValues()[index][value_index]) == 0 && fetchData.getValues()[index].length > 1 && valueList.size() > 1) {
			    			// Do not do anything with last 0 value and remove last time point
			    			//logtimes.remove(logtimes.size()-1);
			    			// ====================================
			    			// Copy last value
			    			valueList.add( valueList.get(valueList.size()-1) );
			    		} else {
				    		valueList.add(
			    				// Round value to 2 decimal points
			    				Math.round(((double) fetchData.getValues()[index][value_index])*100.0)/100.0
				    		);
			    		}
			    	}
			    	node.putIfAbsent(dataSourceNames[index], valueList);
			    }
			    
			    // Add the JSON Object Node to the nodes JSON Array
			    nodes.add(node);
			} catch (FileNotFoundException fe) {
				/* Database not created yet.. Ignore */
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					rrdDatabase.close();
				} catch (Exception e) { /* Ignore */ }
			}
	    }
	    
	    // Write JSON Array nodes to JSON-Holder-Object
	    jsonHolder.putIfAbsent("nodes", nodes);
	    jsonHolder.putIfAbsent("logtimes", logtimes);
	    
	    
	    // Write JSON to file
	    FileWriterTool.writeFile(GlobalVariables.FOLDER_JSON, GlobalVariables.FILENAME_JSON_EXPORT, jsonHolder.toJSONString());
	}
}
