package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PeerList {

	private HeartbeatBuffer hbb;
	ArrayList<InetAddress> upList;
	ArrayList<InetAddress> downList;
	
	int printCounter = 0;
	
	public PeerList(HeartbeatBuffer hbb) {
		this.hbb = hbb;
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
	
	public ArrayList<InetAddress> getUpPeerList() {
		return upList;
	}
	
	public void addPeer (InetAddress peerIP) {
		if (downList.contains(peerIP)) {
			downList.remove(peerIP);
		}
		if (!upList.contains(peerIP)) {
			upList.add(peerIP);
			hbb.addToUpList(peerIP);
		}
		printLists();
	}
	
	public void dropPeer (InetAddress peerIP) {
		if (upList.contains(peerIP)) {
			upList.remove(peerIP);
		}
		if (!downList.contains(peerIP)) {
			downList.add(peerIP);
			hbb.addToDownList(peerIP);
		}
		printLists();
	}
	
	public void printLists () {
		System.out.println("Spice gUrls Status " + printCounter++);
		System.out.println("Up List:");
		for (InetAddress inetAddress : upList) {
			System.out.println(inetAddress);
		}
		System.out.println("Down List: ");
		for (InetAddress inetAddress : downList) {
			System.out.println(inetAddress);
		}
		System.out.println("");
	}
}
