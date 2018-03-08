package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Keeps track of changes to the up nodes and down nodes in the
 * system and reports them when asked.
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 6 March 2018
 */
public class HeartbeatBuffer {
	
	public static final String version = "1.0";
	
	private ArrayList<InetAddress> upList;
	private ArrayList<InetAddress> downList;
	
	/**
	 * Creates an instance of the object HeartbeatBuffer and initializes
	 * all local variables.
	 * @return HeartbeatBuffer object.
	 */
	public HeartbeatBuffer () {
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
	
	/**
	 * Adds the given address to the list of up nodes.
	 * @param upNode the IP address to add to upList
	 */
	public void addToUpList (InetAddress upNode) {
		if(!downList.contains(upNode)){
			upList.add(upNode);
		}
		else{
			downList.remove(upNode);
		}
	}
	
	/**
	 * Adds the given address to the list of down nodes.
	 * @param downNode the IP address to add to downList
	 */
	public void addToDownList (InetAddress downNode) {
		if(!upList.contains(downNode)){
			downList.add(downNode);
		}
		else{
			upList.remove(downNode);
		}
	}
	
	/**
	 * Creates and returns a string representation of packet including
	 * the lists, version, and whether or not this is a join packet.
	 * @return
	 */
	public String getPacket() {
		String packet = ThreadLocalRandom.current().nextInt(10000, 99999) + ";";
		String delim = "";
		for (int i = 0; i < upList.size(); i++) {
			packet += delim + upList.get(i).getHostAddress();
			delim = ",";
		}
		packet += ";";
		
		delim = "";
		for (int i = 0; i < downList.size(); i++) {
			packet += delim + downList.get(i).getHostAddress();
			delim = ",";
		}
		packet += ";";
		emptyLists();
		
		return packet;
	}
	
	/**
	 * Removes everything from both upList and downList.
	 */
	private void emptyLists() {
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
}