package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Keeps track of changes to the up nodes and down nodes in the
 * system and reports them when asked. Further documentation can
 * be found in the P2P version, to which this is nearly identical.
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 7 March 2018
 */
public class HeartbeatBuffer {
	
	public static final String version = "1.0";
	
	private ArrayList<InetAddress> upList;
	private ArrayList<InetAddress> downList;
	
	public HeartbeatBuffer () {
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
	
	public void addToUpList (InetAddress upNode) {
		if(!downList.contains(upNode)){
			upList.add(upNode);
		}
		else{
			downList.remove(upNode);
		}
	}
	
	public void addToDownList (InetAddress downNode) {
		if(!upList.contains(downNode)){
			downList.add(downNode);
		}
		else{
			upList.remove(downNode);
		}
	}
	
	public String getPacket() {
		String packet = version + ";";
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
	
	public boolean isEmpty () {
		return upList.size() == 0 && downList.size() == 0;
		
	}
	
	private void emptyLists () {
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
}