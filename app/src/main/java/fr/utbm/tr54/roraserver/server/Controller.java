package fr.utbm.tr54.roraserver.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import org.json.*;

import fr.utbm.tr54.roraserver.network.BroadcastListener;
import fr.utbm.tr54.roraserver.network.BroadcastManager;

public class Controller implements BroadcastListener,Runnable {
	boolean running;
	HashMap<String,Robot> fleet;
	LinkedBlockingDeque<Request> requestList;
	LinkedBlockingDeque<Robot> queue;
	
	public void start(){
		fleet = new HashMap<String,Robot>();
		requestList = new LinkedBlockingDeque<Request>();
		queue = new LinkedBlockingDeque<Robot>();
	}
	
	@Override
	public void onBroadcastReceived(byte[] message) throws JSONException {
		// TODO Auto-generated method stub
		String messageS = new String(message);
		JSONObject obj = new JSONObject (messageS);
		System.out.println(messageS);
		if (obj.getString("name").equals("Start")) {
			running = true;
		//check if empty list lead to bug
		} else if(!fleet.containsKey(obj.getString("name"))){
			fleet.put(obj.getString("name"), new Robot(obj));
		} else {
			fleet.get(obj.getString("name")).update(obj);
		}
		requestList.addLast(new Request(obj,fleet.get(obj.getString("name"))));
	}
	
	private void parseJSON(JSONObject obj) throws JSONException {
		
		String robotName = obj.getString("name");
		fleet.get(robotName).currentRoute = obj.getInt("currentRoute");
		fleet.get(robotName).isCrossing = obj.getBoolean("isCrossing");
		fleet.get(robotName).isWaiting = obj.getBoolean("isWaiting");
	}

	@Override
	public void run() {
		Request r;
		start();
		while(true){
			//Not sure we need to do that if (problem if message are received during while statement)
			//if (eventList.size() == fleet.size()){
				//copy or reference?
				LinkedBlockingDeque<Request> tempList = new LinkedBlockingDeque<Request>(requestList);
				while(!requestList.isEmpty()){
					requestList.remove();
				}
				while (!tempList.isEmpty()){
					r = tempList.getFirst();
					tempList.removeFirstOccurrence(r);
					if(!r.crossRequest){
						r.host.currentRoute = r.route;
						queue.pollFirst();
					} else if(queue.isEmpty()){
						queue.addLast(r.host);
						JSONObject obj = new JSONObject ();
						try {
							obj.put("name",r.host.name);
							obj.put("isCrossing",true);
							BroadcastManager.getInstance().broadcast(obj.toString().getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if(r.host.currentRoute != queue.getLast().currentRoute && (System.currentTimeMillis() - r.host.requestTime) >= 4000) {
						queue.addLast(r.host);
						JSONObject obj = new JSONObject ();
						try {
							obj.put("name",r.host.name);
							obj.put("isCrossing",true);
							BroadcastManager.getInstance().broadcast(obj.toString().getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if(r.host.currentRoute == queue.getLast().currentRoute){
						queue.addLast(r.host);
						JSONObject obj = new JSONObject ();
						try {
							obj.put("name",r.host.name);
							obj.put("isCrossing",true);
							BroadcastManager.getInstance().broadcast(obj.toString().getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			//}
		}
	}
	

}
