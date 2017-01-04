package fr.utbm.tr54.roraserver.server;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Requests are created for every messages received by the server from the robots
 * Created by Olivier on 29/11/2016.
 */

public class Request {

    String robotName;
    boolean crossRequest;
    boolean route;
    long requestTime;
    long queueTime;

    /**
     * Initialize a request from a JSON Object
     * @param obj
     * @throws JSONException
     */
    public Request(JSONObject obj) throws JSONException {
        robotName = obj.getString("name");
        crossRequest = obj.getBoolean("crossRequest");
        route = obj.getBoolean("currentRoute");
        //Can change : how to check the first request for crossing not to reset time for each request
        if(!obj.getBoolean("isWaiting")){
            requestTime = System.currentTimeMillis();
        }
    }
}
