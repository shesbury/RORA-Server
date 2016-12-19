package fr.utbm.tr54.roraserver.server;

import android.util.Log;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.json.*;

import fr.utbm.tr54.roraserver.network.BroadcastListener;
import fr.utbm.tr54.roraserver.network.BroadcastManager;
import fr.utbm.tr54.roraserver.network.BroadcastReceiver;

/**
 * Controller : handle the request traffic of all robots and sends
 * order to the robots about what to do given their situation.
 */
public class Controller implements BroadcastListener,Runnable {

	//requestList is the list of request sent on the network and received by the server
    ConcurrentLinkedQueue<Request> requestList;
	// The queue is filled with robots that are authorized to cross
    ConcurrentLinkedQueue<Request> queue;
	int msg;
	
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
	
	@Override
	public void onBroadcastReceived(byte[] message) throws JSONException {
		// TODO Auto-generated method stub

		String messageS = new String(message);
		JSONObject obj = new JSONObject (messageS);
		//System.out.println(messageS);

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
		if (obj.has("sender")) {
			return;
		}
		requestList.add(new Request(obj));
	}

	@Override
	public void run() {
		Request r;
		start();
		while(true){
				//copy or reference?
				/*ConcurrentLinkedQueue<Request> tempList = new ConcurrentLinkedQueue<>(requestList);
				while(!requestList.isEmpty()){
					requestList.poll();
				}*/

				while (!requestList.isEmpty()) {
					r = requestList.poll();
					if(!queue.isEmpty())
						if(System.currentTimeMillis() - queue.peek().queueTime > 13000){
							queue.poll();
						}
					if (!r.crossRequest) {
						queue.poll();
					} else {
						if (queue.isEmpty()) {
							queue.add(r);
							r.queueTime = System.currentTimeMillis();
							sendMessage(r.robotName, true, false, queue.size());
					/*} else if(r.route != queue.peek().route && (System.currentTimeMillis() - r.requestTime) >= 15000) {
						queue.add(r);
						r.queueTime = System.currentTimeMillis();
						sendMessage(r.robotName,true,false,queue.size());
						*/

						} else {
							if (r.route == queue.peek().route) {
								queue.add(r);
								r.queueTime = System.currentTimeMillis();
								sendMessage(r.robotName, true, false, queue.size());
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
