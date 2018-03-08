package edu.truman.spicegURLs.node;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Messenger {

	private DatagramSocket socket;
	private Timer timer;
	
	private PeerList pl;
	private HeartbeatBuffer hbb;
	public Thread listener;
	
	public Messenger() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		hbb = new HeartbeatBuffer();
		pl = new PeerList(hbb);
		listener = new Thread(new Listener(pl,this));
		listener.start();
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
		return ThreadLocalRandom.current().nextInt(0, 30);
	}
	
	private void sendChangesToAll (String changes) {
		ArrayList<InetAddress> peers = pl.getListOfAllPeers();
		if (peers.size() > 0) {
			if (Globals.verbose) {
				System.out.println("Sending heartbeat: " + changes);
			}
		}
		for (InetAddress peerIP : peers) {
			try {
				if(!peerIP.equals(InetAddress.getLocalHost())){
					this.sendChangesToPeer(changes, peerIP);
				}
			} catch (UnknownHostException e) {
				System.err.println("Failed to get our own IP in Messenger, exiting...");
				System.exit(1);
			}
		}

	}
	
	private void sendChangesToPeer (String changes, InetAddress peerIP) {
		byte[] data = changes.getBytes();
		try {
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, peerIP, 8585);
			socket.send(sendPacket);
			if (Globals.verbose) {
				System.out.println(".....to " + peerIP.getHostAddress());
			}
		} catch (Exception e) {
			System.err.println("Failed to send packet: " + data + ".....to IP: " + peerIP);
		}
	}
	
	public void addInitialPeer (InetAddress firstPeerIP) {	
		//add the first peer to peer list
		pl.addPeer(firstPeerIP);
	}
	
	public void sendListforJoin(InetAddress IP){
		ArrayList<InetAddress> upList = pl.getUpPeerList();
		byte[] sendMessage;
		
		String packet = ThreadLocalRandom.current().nextInt(10000, 99999) + ";";
		String delim = "";
		for (int i = 0; i < upList.size(); i++) {
			packet += delim + upList.get(i).getHostAddress();
			delim = ",";
		}
		packet += ";;";
		
		if (Globals.verbose) {
			System.out.println("Sending full list heartbeat: " + packet);
		}
		
		sendMessage = packet.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, IP, 8585);
		try {
			socket.send(sendPacket);
			if (Globals.verbose) {
				System.out.println(".....to " + IP.getHostAddress());
			}
		} catch (IOException e) {
			System.err.println("Failed to send packet: " + packet + ".....to IP: " + IP);
		}
		
	}
	
}
