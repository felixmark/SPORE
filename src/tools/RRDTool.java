package tools;

import static org.rrd4j.ConsolFun.AVERAGE;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.json.simple.JSONObject;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

import main.GlobalVariables;
import main.GlobalVariables.WARNING_TYPE;
import object_classes.Node;

// URL for RRD4J Tutorial: https://github.com/rrd4j/rrd4j

public class RRDTool {
	
	public static int MIN_STEPSIZE = 60 * 5; 		// Minimum step size = 5min
	public static int ARCHIVE_SIZE = 12*24*24*100;	// Archive size = 12*24*24*100*(MIN_STEPSIZE*precision) = 100days
	
	public static void createDatabaseIfNotExist(String id) {
		String rrdPath = GlobalVariables.FOLDER_RRD+"nodedata_"+id+".rrd";

		// exit if database already exists
		if (new File(rrdPath).exists()) return;
		
		// Define the RRD Database
		RrdDef rrdDef = new RrdDef(rrdPath, MIN_STEPSIZE);	// minimum Step size = 5m (60 * 5)
		rrdDef.addArchive(AVERAGE, 0.5, 1, ARCHIVE_SIZE);	// 100days of 1h precision
		rrdDef.addArchive(AVERAGE, 0.5, 2, ARCHIVE_SIZE);	// 200days of 2h precision
		rrdDef.addArchive(AVERAGE, 0.5, 4, ARCHIVE_SIZE);	// 400days of 4h precision
		rrdDef.addArchive(AVERAGE, 0.5, 8, ARCHIVE_SIZE);	// 800days of 8h precision
		rrdDef.addDatasource("sunshine", DsType.GAUGE, 60*60*5, 0, 100);
		rrdDef.addDatasource("humidity_top", DsType.GAUGE, 60*60*5, 0, 100);
		rrdDef.addDatasource("humidity_cen", DsType.GAUGE, 60*60*5, 0, 100);
		rrdDef.addDatasource("humidity_bot", DsType.GAUGE, 60*60*5, 0, 100);
		rrdDef.addDatasource("temperature_top", DsType.GAUGE, 60*60*5, 0, 100);
		rrdDef.addDatasource("temperature_bot", DsType.GAUGE, 60*60*5, 0, 100);
		
		// create RRD Database from definition
		RrdDb rrdDb = null;
		try {
			rrdDb = new RrdDb(rrdDef);
			rrdDb.close();
			DebugTool.debug_print(RRDTool.class, "RRD Database "+rrdPath+" created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void writeData(JSONObject jsonObject, Node node) {
		try {
			
			// Get parameters from JSON Object
			final String id = (String) jsonObject.get("id");
		    final long sunshine = (long) jsonObject.get("sunshine");
		    final double humidity_top = (double) jsonObject.get("humidity_top");
		    final double humidity_cen = (double) jsonObject.get("humidity_cen");
		    final double humidity_bot = (double) jsonObject.get("humidity_bot");
		    final double temperature_top = (double) jsonObject.get("temperature_top");
		    final double temperature_bot = (double) jsonObject.get("temperature_bot");
		    
		    
		    
		    // Check if any values exceed boundaries and set warnings!
		    if (node != null) {
		    	// If node already exists
		    	
		    	// Check if any humidity values are bad
		    	if (node.getMin_humidity_top() != -1 && node.getMin_humidity_top() > humidity_top) {
		    		node.addWarning(WARNING_TYPE.TOO_DRY_TOP);
		    	} else {
		    		node.removeWarning(WARNING_TYPE.TOO_DRY_TOP);
		    	}
		    	if (node.getMin_humidity_cen() != -1 && node.getMin_humidity_cen() > humidity_top) {
		    		node.addWarning(WARNING_TYPE.TOO_DRY_CEN);
		    	} else {
		    		node.removeWarning(WARNING_TYPE.TOO_DRY_CEN);
		    	}
		    	if (node.getMin_humidity_bot() != -1 && node.getMin_humidity_bot() > humidity_top) {
		    		node.addWarning(WARNING_TYPE.TOO_DRY_BOT);
		    	} else {
		    		node.removeWarning(WARNING_TYPE.TOO_DRY_BOT);
		    	}
		    	
		    	// add Sunshine value
		    	Date now = new Date();
		    	if (now.getHours() >= 6 && now.getHours() < GlobalVariables.TIME_SEND_MAIL_INT) {
		    		node.addSunshineValue(sunshine);
		    	}
		    }
		    
		    
			// Get RRD Database Path
			final String rrdPath = GlobalVariables.FOLDER_RRD+"nodedata_"+id+".rrd";
	
			// Write data to database
			RrdDb rrdDb = new RrdDb(rrdPath);
			Sample sample = rrdDb.createSample();
			
		    sample.setValue("sunshine", sunshine);
		    sample.setValue("humidity_top", humidity_top);
		    sample.setValue("humidity_cen", humidity_cen);
		    sample.setValue("humidity_bot", humidity_bot);
		    sample.setValue("temperature_top", temperature_top);
		    sample.setValue("temperature_bot", temperature_bot);
		    sample.update();
		    
			// Close database
		    rrdDb.close();
			DebugTool.debug_print(RRDTool.class, "Updated RRD Database "+rrdPath+".");
			
			// Create RRD Graph
			//createGraph(id);	// export image of last 24 hours
			
			// Create Google Charts Graph
			GoogleChartsCreatorTool.createGoogleChartsJSON();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/*
	// ALTER EXPORT (nicht mehr verwendet)
	
	@SuppressWarnings("unused")
	private static void createGraph(String id) {
		final String rrdPath = GlobalVariables.FOLDER_RRD+"nodedata_"+id+".rrd";
		
		// View last 24 hours
		Calendar calendar = Calendar.getInstance();
	    //calendar.add(Calendar.DATE, -1);
		calendar.add(Calendar.SECOND, (int) GlobalVariables.BACKLOGTIME_FROM);
		
		// Create a graph definition
		RrdGraphDef gDef = new RrdGraphDef();
		gDef.setWidth(500);
		gDef.setHeight(300);
		gDef.setStartTime(calendar.getTimeInMillis()/1000);
		gDef.setFilename(GlobalVariables.FOLDER_EXPORT+"nodedata_"+id+".png");
		gDef.setTitle("Plant report");
		gDef.setVerticalLabel("%/°C");
		gDef.setAltAutoscale(true); // autoscale the graph
		
		// Add all Datalines
		gDef.datasource("mysunshine", rrdPath, "sunshine", ConsolFun.AVERAGE);
		gDef.line("mysunshine", Color.BLACK, "Sunshine in %");
		gDef.datasource("myhumidity_top", rrdPath, "humidity_top", ConsolFun.AVERAGE);
		gDef.line("myhumidity_top", Color.BLUE, "Humidity top in %");
		gDef.datasource("myhumidity_cen", rrdPath, "humidity_cen", ConsolFun.AVERAGE);
		gDef.line("myhumidity_cen", Color.CYAN, "Humidity center in %");
		gDef.datasource("myhumidity_bot", rrdPath, "humidity_bot", ConsolFun.AVERAGE);
		gDef.line("myhumidity_bot", Color.GRAY, "Humidity bottom in %");
		gDef.datasource("mytemperature_top", rrdPath, "temperature_top", ConsolFun.AVERAGE);
		gDef.line("mytemperature_top", Color.GREEN, "Temperature air in %");
		gDef.datasource("mytemperature_bot", rrdPath, "temperature_bot", ConsolFun.AVERAGE);
		gDef.line("mytemperature_bot", Color.RED, "Temperature soil in %");
		gDef.setImageFormat("png");

		// Draw the graph
		try {
			@SuppressWarnings("unused")
			RrdGraph graph = new RrdGraph(gDef);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
}
