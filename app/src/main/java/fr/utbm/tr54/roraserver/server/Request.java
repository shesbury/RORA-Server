package fr.utbm.tr54.roraserver.server;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
/**
 * Created by Olivier on 29/11/2016.
 */

public class Request {

    String robotName;
    boolean crossRequest;
    boolean route;
    long requestTime;
    long queueTime;
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
