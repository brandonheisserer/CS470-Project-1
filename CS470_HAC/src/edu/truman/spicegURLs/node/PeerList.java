package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.util.ArrayList;

public class PeerList {

	private HeartbeatBuffer hbb;
	ArrayList<InetAddress> peerList;
	
	public PeerList(HeartbeatBuffer hbb) {
		this.hbb = hbb;
		peerList = new ArrayList<>();
	}
	
	public ArrayList<InetAddress> getPeerList() {
		return peerList;
	}
	
	public void addInitialPeer (InetAddress peerIP) {
		peerList.add(peerIP);
	}
	
	public void addPeer (InetAddress peerIP) {
		peerList.add(peerIP);
		hbb.queueChange("U" + peerIP.getHostAddress());
	}
	
	public void dropPeer (InetAddress peerIP) {
		peerList.remove(peerIP);
		hbb.queueChange("D" + peerIP.getHostAddress());
	}
}
