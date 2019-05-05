package object_classes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONObject;

import main.GlobalVariables;
import main.GlobalVariables.WARNING_TYPE;
import tools.DebugTool;
import tools.NodeHandlerTool;

/**
 * The Node class holds information about plant measuring stations like
 * id, name, creation time, minimum and maximum values for sending mails and
 * the average sunshineValue as well as warnings set for this station that will be sent to the user.
 *
 */
public class Node implements Serializable {
	
	//======================== Private Variables ========================
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private Date creationTime;
	
	private double min_humidity_top;
	private double min_humidity_cen;
	private double min_humidity_bot;
	private double min_daily_sun;
	private double max_daily_sun;
	private ArrayList<Double> sunshineValues;
	
	private ArrayList<GlobalVariables.WARNING_TYPE> warnings = new ArrayList<GlobalVariables.WARNING_TYPE>();


	//======================== NODE METHODS ========================
	public Node(String id) {
		// Initialize Variables
		this.setId(id);
		creationTime = new Date();
		
		min_humidity_top = -1;
		min_humidity_cen = -1;
		min_humidity_bot = -1;
		min_daily_sun = -1;
		max_daily_sun = -1;
		createSunshineValuesArrayList();
		createWarningsArrayList();
		
		DebugTool.debug_print(Node.class,id+" was created.");
	}
	
	@SuppressWarnings("unchecked")
	public String toString() {
		// Write Parameters to JSON Object String
		JSONObject jO = new JSONObject();
		jO.putIfAbsent("id", id);
		jO.putIfAbsent("name", name);
		jO.putIfAbsent("min_humidity_top", min_humidity_top);
		jO.putIfAbsent("min_humidity_cen", min_humidity_cen);
		jO.putIfAbsent("min_humidity_bot", min_humidity_bot);
		jO.putIfAbsent("min_daily_sun", min_daily_sun);
		jO.putIfAbsent("max_daily_sun", max_daily_sun);
		return jO.toJSONString();
	}
	
	/**
	 * Set values from a JSONObject.
	 * @param jsonObject
	 */
	public void setValues(JSONObject jsonObject) {
		setName((String) jsonObject.get("name"));
		setMin_humidity_top((long) jsonObject.get("min_humidity_top"));
		setMin_humidity_cen((long) jsonObject.get("min_humidity_cen"));
		setMin_humidity_bot((long) jsonObject.get("min_humidity_bot"));
		setMin_daily_sun((long) jsonObject.get("min_daily_sun"));
		setMax_daily_sun((long) jsonObject.get("max_daily_sun"));
		NodeHandlerTool.writeNodesToFile();
	}
	
	public void addSunshineValue(double value) {
		createSunshineValuesArrayList();
		sunshineValues.add(new Double(value));
	}
	
	public void clearSunshineValues() {
		createSunshineValuesArrayList();
		sunshineValues.clear();
	}

	public double getAverageSun() {
		if (sunshineValues == null) return -1;
		if (sunshineValues.size() == 0) return -1;
		double average = 0;
		for (Double val : sunshineValues) {
			if (val.isNaN()) continue;
			average = average + val.doubleValue();
		}
		return (average / sunshineValues.size());
	}
	
	
	//======================== AUTOMATICALLY GENERATED CODE ========================
	
	public Date getCreationTime() {
		return creationTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public double getMin_humidity_top() {
		return min_humidity_top;
	}

	public void setMin_humidity_top(double min_humidity_top) {
		this.min_humidity_top = min_humidity_top;
	}

	public double getMin_humidity_cen() {
		return min_humidity_cen;
	}

	public void setMin_humidity_cen(double min_humidity_cen) {
		this.min_humidity_cen = min_humidity_cen;
	}

	public double getMin_humidity_bot() {
		return min_humidity_bot;
	}

	public void setMin_humidity_bot(double min_humidity_bot) {
		this.min_humidity_bot = min_humidity_bot;
	}

	public double getMin_daily_sun() {
		return min_daily_sun;
	}

	public void setMin_daily_sun(double min_daily_sun) {
		this.min_daily_sun = min_daily_sun;
	}

	public double getMax_daily_sun() {
		return max_daily_sun;
	}

	public void setMax_daily_sun(double max_daily_sun) {
		this.max_daily_sun = max_daily_sun;
	}

	public ArrayList<GlobalVariables.WARNING_TYPE> getWarnings() {
		return warnings;
	}

	public void setWarnings(ArrayList<GlobalVariables.WARNING_TYPE> warnings) {
		createWarningsArrayList();
		this.warnings = warnings;
	}
	
	public void addWarning(GlobalVariables.WARNING_TYPE warning) {
		createWarningsArrayList();
		if (!warnings.contains(warning)) warnings.add(warning);
	}
	
	public void removeWarning(GlobalVariables.WARNING_TYPE warning) {
		createWarningsArrayList();
		warnings.remove(warning);
	}
	
	private void createSunshineValuesArrayList() {
		if (sunshineValues == null) sunshineValues = new ArrayList<Double>();
	}
	
	private void createWarningsArrayList() {
		if (warnings == null) warnings = new ArrayList<WARNING_TYPE>();
	}

	public void clearWarnings() {
		createWarningsArrayList();
		warnings.clear();
	}
}
