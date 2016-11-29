package fr.utbm.tr54.roraserver.server;

import org.json.JSONException;
import org.json.JSONObject;

public class Robot {
	String name;
	//Route is 0 or 1 defined at the start
	int currentRoute;
	//when robot is in the CrossingQueue
	boolean isCrossing;
	//When the robot is waiting
	boolean isWaiting;

	//request time
	long requestTime;
	
	public Robot(JSONObject obj) throws JSONException {
		this.name = obj.getString("name");
		this.currentRoute = obj.getInt("currentRoute");
		this.isCrossing = obj.getBoolean("isCrossing");
		this.isWaiting = obj.getBoolean("isWaiting");
	}

	public void update(JSONObject obj) throws JSONException {
		this.name = obj.getString("name");
		this.currentRoute = obj.getInt("currentRoute");
		this.isCrossing = obj.getBoolean("isCrossing");
		this.isWaiting = obj.getBoolean("isWaiting");
	}
}
