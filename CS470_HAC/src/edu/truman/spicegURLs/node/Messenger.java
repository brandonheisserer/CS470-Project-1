package edu.truman.spicegURLs.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.*;
import javax.swing.Timer;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Sends out heartbeats with all new changes to all peers at
 * given intervals. Further documentation can
 * be found in the P2P version, to which this is nearly identical.
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 7 March 2018
 */
public class Messenger implements Runnable {

	private DatagramSocket socket;
	private Timer time;
	
	private PeerList pl;
	private HeartbeatBuffer hbb;
	private boolean isServer; // Specific to ClientServer
	private InetAddress serverAddress; //Specific to ClientServer
	
	public Thread listener;
	
	public Messenger (boolean isServer, InetAddress serverAddress) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.isServer = isServer;
		hbb = new HeartbeatBuffer();
		pl = new PeerList(hbb);
		listener = new Thread(new Listener(pl, this, isServer));
		listener.start();
		this.serverAddress = serverAddress;
		
		time = new Timer(getInterval()*1000, new ActionListener(){
			/**
			 * This is distinguished from the P2P version in that the
			 * timer is not needed by the server.
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String packet = "1.0;;;";
				Messenger.this.sendChangesToPeer(packet,serverAddress);
				Messenger.this.clientHeartbeat();
			}
		});
	}
	
	/**
	 * This sends peer information to peers. P2P version does not
	 * have an equivalent because it does need to send server messages
	 * to peers.
	 */
	public void run () {
		String packet = null;
		while (true) {
			if (!hbb.isEmpty()) {
			  packet = hbb.getPacket();
				  
				if (isServer) {
					Messenger.this.sendChangesToAll(packet);
				}
				else {
					Messenger.this.sendChangesToPeer(packet, serverAddress);
				}
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * This serves to initialize the client heartbeat and sends
	 * the initial join message to the server. P2P has no equivalent.
	 */
	public void initClientHeartbeat () {
		String packet = hbb.getPacket();
		this.sendChangesToPeer(packet, serverAddress);
		time.start();
	}
	
	/**
	 * Changes the delay that the client will send its next message on.
	 */
	public void clientHeartbeat(){
		time.setDelay(getInterval() *1000);
	}
	
	public void addInitialPeer (InetAddress firstPeerIP) {	
		pl.addPeer(firstPeerIP);
	}
	
	public void sendListforJoin (InetAddress IP){
		ArrayList<InetAddress> upList = pl.getUpPeerList();
		byte[] sendMessage;
		String packet = "1.0;";
		String delim = "";
		for (int i = 0; i < upList.size(); i++) {
			if (!upList.get(i).equals(IP)) {
				packet += delim + upList.get(i).getHostAddress();
				delim = ",";
			}
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
			e.printStackTrace();
		}
	}
	
	private int getInterval () {
		return ThreadLocalRandom.current().nextInt(0, 30);
	}
	
	private void sendChangesToAll (String changes) {
		ArrayList<InetAddress> peers = pl.getListOfAllPeers();
		if (peers.size() > 0 && Globals.verbose) {
			System.out.println("Sending heartbeat: " + changes);
		}
		for (InetAddress peerIP : peers) {
			try {
				if (!peerIP.equals(InetAddress.getLocalHost())) {
					this.sendChangesToPeer(changes, peerIP);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
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
			e.printStackTrace();
		}
	}
}
