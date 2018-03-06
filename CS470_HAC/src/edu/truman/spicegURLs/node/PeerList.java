package edu.truman.spicegURLs.node;

import java.net.InetAddress;
import java.util.ArrayList;
import javax.print.attribute.standard.PagesPerMinute;

public class PeerList {

	private HeartbeatBuffer hbb;
	ArrayList<InetAddress> upList;
	ArrayList<InetAddress> downList;
	
	public PeerList(HeartbeatBuffer hbb) {
		this.hbb = hbb;
		upList = new ArrayList<>();
		downList = new ArrayList<>();
	}
	
	public ArrayList<InetAddress> getUpPeerList() {
		return upList;
	}
	
	/*public void addInitialPeer (InetAddress peerIP) {
		peerList.add(peerIP);
	}*/
	
	public void addPeer (InetAddress peerIP) {
		if (downList.contains(peerIP)) {
			downList.remove(peerIP);
		}
		if (!upList.contains(peerIP)) {
			upList.add(peerIP);
			//hbb.queueChange(peerIP);
		}
	}
	
	public void dropPeer (InetAddress peerIP) {
		if (upList.contains(peerIP)) {
			upList.remove(peerIP);
		}
		if (!downList.contains(peerIP)) {
			downList.add(peerIP);
			//queuechange
		}
	}
}
