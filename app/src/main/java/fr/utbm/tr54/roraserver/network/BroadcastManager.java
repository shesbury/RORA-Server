package fr.utbm.tr54.roraserver.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Singleton class used to send broadcast messages
 * @author Alexandre Lombard
 */
public class BroadcastManager implements AutoCloseable {
	
	private static BroadcastManager instance = null;
	
	/**
	 * Gets an instance of the broadcast manager 
	 * @return the broadcast manager
	 * @throws SocketException
	 */
	public static BroadcastManager getInstance() throws SocketException {
		if(instance == null) {
			instance = new BroadcastManager();
		}
		
		return instance;
	}
	
	private DatagramSocket socket;
	
	private BroadcastManager() throws SocketException {
		this.socket = new DatagramSocket();
	}
	
	/**
	 * Close the broadcast manager
	 */
	public void close() {
		this.socket.close();
	}
	
	/**
	 * Broadcast a raw message
	 * @param message the message
	 * @throws IOException thrown if unable to send the packet
	 */
	public void broadcast(byte[] message) throws IOException {
		try {
			final DatagramPacket datagramPacket = new DatagramPacket(message, message.length, InetAddress.getByName("192.168.43.255"), 8888);
			
			this.socket.send(datagramPacket);
		} catch (UnknownHostException e) {
			//
		}
	}
}
