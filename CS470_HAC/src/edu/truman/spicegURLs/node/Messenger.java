package edu.truman.spicegURLs.node;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.midi.Soundbank;

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
		//return ThreadLocalRandom.current().nextInt(0, 31);
		return 1;
	}
	
	private void sendChangesToAll (String changes) {
		ArrayList<InetAddress> peers = pl.getUpPeerList();
		if (peers.size() > 0) {
			System.out.println("Heartbeat: No new changes");
		} else {
			System.out.println("Heartbeat: Sending changes: " + changes);
		}
		for (InetAddress peerIP : peers) {
			try {
				if(!peerIP.equals(InetAddress.getLocalHost())){
					this.sendChangesToPeer(changes, peerIP);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private void sendChangesToPeer (String changes, InetAddress peerIP) {
		byte[] data = changes.getBytes();
		try {
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, peerIP, 9876);
			socket.send(sendPacket);
			System.out.println(".....to " + peerIP.getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addInitialPeer (InetAddress firstPeerIP) {	
		//add the first peer to peer list
		pl.addPeer(firstPeerIP);
		try {
			pl.addPeer(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendListforJoin(InetAddress IP){
		ArrayList<InetAddress> data = pl.getUpPeerList();
		String message = "";
		byte[] sendMessage;
		message += "1.0;";
		for(int i = 0; i < data.size(); i++){
			message += data.get(i).getHostAddress();
			message += ",";
		}
		message += ';';
		System.out.println("Full list heartbeat: " + message);
		sendMessage = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, IP, 9876);
		try {
			socket.send(sendPacket);
			System.out.println(".....to " + IP.getHostAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
