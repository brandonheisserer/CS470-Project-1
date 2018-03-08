package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Keeps a list of all peers that are currently alive,
 * and another list of the peers that are down.
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 7 March 2018
 */
public class PeerList {

	private HeartbeatBuffer hbb;
	
	ArrayList<InetAddress> upList;
	ArrayList<InetAddress> downList;
	
	private InetAddress ourIP;
	private int printCounter = 0;
	
	/**
	 * Creates an instance of the object PeerList and initializes
	 * all local variables.
	 * @param hbb reference to the heartbeat buffer
	 * @return PeerList object
	 */
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
	
	/**
	 * Returns an ArrayList of all of the peers that 
	 * are alive.
	 * @return list of alive peers
	 */
	public ArrayList<InetAddress> getUpPeerList() {
		return upList;
	}
	
	/**
	 * Returns an ArrayList of all of the peers, whether 
	 * they are alive or down.
	 * @return list of all peers
	 */
	public ArrayList<InetAddress> getListOfAllPeers() {
		ArrayList<InetAddress> temp = new ArrayList<>();
		temp.addAll(upList);
		temp.addAll(downList);
		return temp;
	}
	
	/**
	 * Adds a peer to the up list, and removes them 
	 * from the down list if they were.
	 * @param peerIP ip of the peer to add
	 */
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
	
	/**
	 * Adds a peer to the down list, and removes them 
	 * from the up list if they were.
	 * @param peerIP ip of the peer to add
	 */
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
	
	/**
	 * Prints out both the up list and the down list.
	 */
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
