package fr.utbm.tr54.roraserver.server;

import org.json.JSONException;
import org.json.JSONObject;

public class Robot {
	String name;
	//Route is 0 or 1 defined at the start
	boolean currentRoute;
	//when robot is in the CrossingQueue
	boolean isCrossing;
	//When the robot is waiting
	boolean isWaiting;

	//request time
	long requestTime;
	
	public Robot(JSONObject obj) throws JSONException {
		if (obj.has("name")) {
			this.name = obj.getString("name");
		}

		if (obj.has("currentRoute")) {
			this.currentRoute = obj.getBoolean("currentRoute");
		}

		if (obj.has("isCrossing")) {
			this.isCrossing = obj.getBoolean("isCrossing");
		}

		if (obj.has("isWaiting")) {
			this.isWaiting = obj.getBoolean("isWaiting");
		}
	}

	/**
	 * update the status of a robot when receiving a JSON request
	 * @param obj
	 * @throws JSONException
     */
	public void update(JSONObject obj) throws JSONException {
		if (obj.has("name")) {
			this.name = obj.getString("name");
		}

		if (obj.has("currentRoute")) {
			this.currentRoute = obj.getBoolean("currentRoute");
		}

		if (obj.has("isCrossing")) {
			this.isCrossing = obj.getBoolean("isCrossing");
		}

		if (obj.has("isWaiting")) {
			this.isWaiting = obj.getBoolean("isWaiting");
		}
	}
}
