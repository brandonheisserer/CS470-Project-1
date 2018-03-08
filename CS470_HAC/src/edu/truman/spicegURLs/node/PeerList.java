package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PeerList {

	private HeartbeatBuffer hbb;
	ArrayList<InetAddress> upList;
	ArrayList<InetAddress> downList;
	private InetAddress ourIP;
	
	int printCounter = 0;
	
	public PeerList(HeartbeatBuffer hbb) {
		this.hbb = hbb;
		upList = new ArrayList<>();
		downList = new ArrayList<>();
		try {
			ourIP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			System.out.println("Can't find our IP, exiting...");
			System.exit(1);
		}
	}
	
	public ArrayList<InetAddress> getUpPeerList() {
		return upList;
	}
	
	public ArrayList<InetAddress> getListOfAllPeers() {
		ArrayList<InetAddress> temp = new ArrayList<>();
		temp.addAll(upList);
		temp.addAll(downList);
		return temp;
	}
	
	public void addPeer (InetAddress peerIP) {
		if (ourIP.equals(peerIP)){
			return;
		}
		
		if (downList.contains(peerIP)) {
			downList.remove(peerIP);
		}
		if (!upList.contains(peerIP)) {
			upList.add(peerIP);
			hbb.addToUpList(peerIP);
			printLists();
		}
	}
	
	public void dropPeer (InetAddress peerIP) {
		if (ourIP.equals(peerIP)){
			return;
		}
		
		if (upList.contains(peerIP)) {
			upList.remove(peerIP);
		}
		if (!downList.contains(peerIP)) {
			downList.add(peerIP);
			hbb.addToDownList(peerIP);
			printLists();
		}
	}
	
	public void printLists () {
		System.out.println("\n---Begin status update #" + printCounter);
		System.out.println(upList.size() + " up list entries:");
		for (InetAddress inetAddress : upList) {
			System.out.println(inetAddress);
		}
		System.out.println(downList.size() + " down list entries:");
		for (InetAddress inetAddress : downList) {
			System.out.println(inetAddress);
		}
		System.out.println("---End status update #" + printCounter + "\n");
		printCounter++;
	}
}
