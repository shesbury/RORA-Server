package fr.utbm.tr54.roraserver.server;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
/**
 * Created by Olivier on 29/11/2016.
 */

public class Request {

    Robot host;
    boolean crossRequest;
    int route;
    public Request(JSONObject obj,Robot r) throws JSONException {
        host = r;
        crossRequest = obj.getBoolean("CrossRequest");
        route = obj.getInt("currentRoute");
        //Can change : how to check the first request for crossing not to reset time for each request
        if(!host.isWaiting){
            host.requestTime = System.currentTimeMillis();
        }
    }
}
