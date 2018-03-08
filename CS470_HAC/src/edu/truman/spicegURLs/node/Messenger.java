package edu.truman.spicegURLs.node;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Sends out heartbeats with all new changes to all peers 
 * at given intervals. 
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 7 March 2018
 */
public class Messenger {

	private DatagramSocket socket;
	private Timer timer;
	
	private PeerList pl;
	private HeartbeatBuffer hbb;
	public Thread listener;
	
	/**
	 * Creates an instance of the object Messenger and initializes
	 * all local variables. It also starting the listener thread.
	 * @return HeartbeatBuffer object
	 */
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
	
	/**
	 * Grabs the latest changes and sends heartbeats to 
	 * all peers at intervals.
	 */
	public void waitToSendNextHeartbeat (boolean firstInstant) {
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  // pull info from heartbeat buffer
				  String packet = hbb.getPacket();
				  
				  // send data to all peers on list
				  Messenger.this.sendChangesToAll(packet);
				  
				  // wait again
				  Messenger.this.waitToSendNextHeartbeat(false);
			  }
			}, getInterval(firstInstant)*1000);
	}
	
	/**
	 * Get a random number between 0 to 30
	 * @return random int between 0 and 30
	 */
	private int getInterval (boolean instant) {
		return instant ? 1 : ThreadLocalRandom.current().nextInt(0, 30);
	}
	
	/**
	 * Sends the given packet to all peers
	 * @param changes the packet to send
	 */
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
	
	/**
	 * Sends the given packet to the peer at the given IP 
	 * address
	 * @param changes changes the packet to send
	 * @param peerIP the ip address of the peer
	 */
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
	
	/**
	 * Adds the first peer to the peer list
	 * @param firstPeerIP the ip address of the peer
	 */
	public void addInitialPeer (InetAddress firstPeerIP) {	
		pl.addPeer(firstPeerIP);
	}
	
	/**
	 * Sends a list of all up peers to the peer at the 
	 * given ip address
	 * @param IP
	 */
	public void sendListforJoin(InetAddress IP){
		ArrayList<InetAddress> upList = pl.getUpPeerList();
		byte[] sendMessage;
		
		String packet = "1.0;";
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
			System.err.println("Failed to send packet: " + packet + "\n.....to IP: " + IP);
		}
		
	}
	
}
