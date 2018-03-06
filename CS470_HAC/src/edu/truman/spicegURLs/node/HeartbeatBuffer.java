package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.util.ArrayList;

public class HeartbeatBuffer {
	
	public static final String version = "1.0";
	
	private boolean join;
	private ArrayList<InetAddress> upList;
	private ArrayList<InetAddress> downList;
	
	public HeartbeatBuffer () {
		join = true;
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
	
	public void addToUpList (InetAddress upNode) {
		upList.add(upNode);
	}
	
	public void addToDownList (InetAddress downNode) {
		downList.add(downNode);
	}
	
	public String getPacket() {
		String packet = version + ";";
		if (join) {
			packet += "1;";
			join = false;
		} else {
			packet += "0;";
		}
		for (int i = 0; i < upList.size(); i++) {
			packet += upList.get(i).getHostAddress() + ",";
		}
		packet += ";";
		for (int i = 0; i < downList.size(); i++) {
			packet += downList.get(i).getHostAddress() + ",";
		}
		
		emptyLists();
		
		return packet;
	}
	
	private void emptyLists() {
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
}
