package fr.utbm.tr54.roraserver.network;

import org.json.JSONException;

/**
 * Broadcast listener interface
 * @author Alexandre Lombard
 */
public interface BroadcastListener {
	/**
	 * Triggered on broadcast received
	 * @param message the raw message
	 */
	public void onBroadcastReceived(byte[] message) throws JSONException;
}
