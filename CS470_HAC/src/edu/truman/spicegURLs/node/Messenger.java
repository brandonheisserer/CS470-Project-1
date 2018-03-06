package edu.truman.spicegURLs.node;

import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Messenger {

	private DatagramSocket socket;
	private Timer timer;
	
	private PeerList pl;
	private HeartbeatBuffer hbb;
	private Listener listener;
	
	public Messenger() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		hbb = new HeartbeatBuffer();
		pl = new PeerList(hbb);
		listener = new Listener(pl);
		listener.run();
		timer = new Timer();
	}
	
	public void waitToSendNextHeartbeat () {
		
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  // pull info from heartbeat buffer
				  String packet = hbb.getPacket();
				  
				  // send data to all peers on list
				  Messenger.this.sendChangesToAll(packet);
				  
				  // wait again
				  Messenger.this.waitToSendNextHeartbeat();
			  }
			}, getInterval()*1000);
	}
	
	private int getInterval () {
		// random number between 0 and 30
		return ThreadLocalRandom.current().nextInt(0, 31);
	}
	
	private void sendChangesToAll (String changes) {
		ArrayList<InetAddress> peers = pl.getUpPeerList();
		for (InetAddress peerIP : peers) {
			this.sendChangesToPeer(changes, peerIP);
		}
	}
	
	private void sendChangesToPeer (String changes, InetAddress peerIP) {
		byte[] data = changes.getBytes();
		try {
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, peerIP, 9876);
			socket.send(sendPacket);
			System.out.println("Changes of length " + changes.length() + " sent to " + peerIP.getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addInitialPeer (InetAddress firstPeerIP) {
		//add ourselves to heartbeat buffer
		hbb.addToUpList(socket.getInetAddress());
		
		//add the first peer to peer list
		pl.addPeer(firstPeerIP);
	}
	
}