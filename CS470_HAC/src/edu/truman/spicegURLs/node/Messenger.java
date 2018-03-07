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
				  System.out.println("Sending heartbeat...");
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
		//return ThreadLocalRandom.current().nextInt(0, 31);
		return 1;
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
	public void sendListforJoin(InetAddress IP){
		ArrayList<InetAddress> data = pl.getUpPeerList();
		String message = "";
		byte[] sendMessage;
		for(int i = 0; i < data.size(); i++){
			message += data.get(i).getHostAddress();
		}
		sendMessage = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, IP, 9876);
		try {
			socket.send(sendPacket);
			System.out.println(IP.getHostAddress() + "joined");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
