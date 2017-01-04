package fr.utbm.tr54.roraserver.server;

import android.util.Log;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.*;

import fr.utbm.tr54.roraserver.network.BroadcastListener;
import fr.utbm.tr54.roraserver.network.BroadcastManager;
import fr.utbm.tr54.roraserver.network.BroadcastReceiver;

/**
 * Controller : handle the request traffic of all robots and sends
 * order to the robots about what to do given their situation.
 * @author Olivier
 */
public class Controller implements BroadcastListener,Runnable {

	//requestList is the list of request sent on the network and received by the server
    ConcurrentLinkedQueue<Request> requestList;
	// The queue is filled with robots that are authorized to cross
    ConcurrentLinkedQueue<Request> queue;
	// message counter
	int msg;

	/**
	 * Initialize message count, add Controller as a listener of the broadcast system
	 * and create the lists
	 */
	public void start(){
		try {
			msg = 0;
			BroadcastReceiver.getInstance().addListener(this);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		requestList = new ConcurrentLinkedQueue<Request>();
		queue = new ConcurrentLinkedQueue<Request>();
	}

	/**
	 * Manage all messages on the network.
	 * If the message is from the server then it's ignored, otherwise it's added to
	 * the requestList.
	 * @param message the raw message
	 * @throws JSONException
     */
	public void onBroadcastReceived(byte[] message) throws JSONException {

		String messageS = new String(message);
		JSONObject obj = new JSONObject (messageS);

        if (obj.has("sender")) {
            Log.i("I/", "MESSAGE FROM THE SERVER");
        }

        msg=msg+1;
		Log.i("I/json msg ",msg + " : " + obj.getString("name"));

        if (obj.has("position")) {
            Log.i("I/json position ", String.valueOf(obj.getInt("position")));
        }

		if (obj.has("isWaiting")) {
			Log.i("I/json isWaiting ", String.valueOf(obj.getBoolean("isWaiting")));
		}

		if (obj.has("isCrossing")) {
			Log.i("I/json isCrossing ", String.valueOf(obj.get("isCrossing")));
		}

		if (obj.has("crossRequest")) {
			Log.i("I/json crossR ", String.valueOf(obj.getBoolean("crossRequest")));
		}

		// message from the server
		if (obj.has("sender") || obj.getString("name").equals("INIT")) {
			return;
		}
		requestList.add(new Request(obj));
	}

	@Override
	/**
	 * Loop of the controller that manage the requests and send messages/orders to the robots
	 * corresponding to their status
	 */
	public void run() {
		Request r;
		start();
		//infinite loop
		while(true){
				//Server has to handle every single request
				while (!requestList.isEmpty()) {
					//Server takes the first request
					r = requestList.poll();

					//Server check if there is robots crossing now
					if(!queue.isEmpty())
						//this security may cause problems but we added it to make sure a robot is not in queue forever.
						//if the first robot of the queue is here for too long then we remove it from the crossing queue.
						if(System.currentTimeMillis() - queue.peek().queueTime > 20000){
							queue.poll();
						}
					//if it's not a cross request then it's a notification that the robot exits the intersection
					//so the server remove it from the queue.
					if (!r.crossRequest) {
						queue.poll();
					//if this is a cross request :
					} else {
						//first case: queue is empty --> the way is clear. Robot can go.
						if (queue.isEmpty()) {
							queue.add(r);
							r.queueTime = System.currentTimeMillis();
							sendMessage(r.robotName, true, false, queue.size());
						} else {
							//second case: the route is the same as the busy one --> Robot can go.
							if (r.route == queue.peek().route) {
								queue.add(r);
								r.queueTime = System.currentTimeMillis();
								sendMessage(r.robotName, true, false, queue.size());
							//last case: the Robot can't cross, server send him to wait
							} else {
								sendMessage(r.robotName, false, true, 0);
							}
						}
					}
				}
		}
	}

	/**
	 * Send a message to a robot on the network(robot specified by his name)
	 * message : {JSON message {sender}{name}{isCrossing}{isWaiting}{position}}
	 * @param robotName
	 * @param isCrossing
	 * @param isWaiting
     * @param position *0 when we don't send the position
     */
	public void sendMessage(String robotName, boolean isCrossing, boolean isWaiting,int position){
		JSONObject obj = new JSONObject();
		try {
			obj.put("sender", "server");
			obj.put("name",robotName);
			obj.put("isCrossing", isCrossing);
			obj.put("isWaiting", isWaiting);
			if(position != 0)
				obj.put("position",position);
			BroadcastManager.getInstance().broadcast(obj.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
