package edu.truman.spicegURLs.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.*;
import javax.swing.Timer;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.midi.Soundbank;

public class Messenger implements Runnable {

	private DatagramSocket socket;
	private Timer time;
	
	private PeerList pl;
	private HeartbeatBuffer hbb;
	private boolean isServer;
	InetAddress serverIP;
	public Thread listener;
	
	public Messenger(boolean isServer, InetAddress serverIP) {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.isServer = isServer;
		hbb = new HeartbeatBuffer();
		pl = new PeerList(hbb);
		listener = new Thread(new Listener(pl,this,isServer));
		listener.start();
		this.serverIP = serverIP;
		time = new Timer(getInterval()*1000, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String packet = hbb.getPacket();
				Messenger.this.sendChangesToPeer(packet,serverIP);
				Messenger.this.clientHeartbeat();
			}
			
		});
	}
	
	public void run () {
		String packet = null;
		while(true){
			if(!hbb.isempty()){
			  packet = hbb.getPacket();
				  
				// send data to appropriate peers on list
				if(isServer){
					Messenger.this.sendChangesToAll(packet);
				}
				else{
					Messenger.this.sendChangesToPeer(packet, serverIP);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void initClientHeartbeat(){
		time.start();
	}
	public void clientHeartbeat(){
		time.setDelay(getInterval() *1000);
	}
	
	private int getInterval () {
		// random number between 0 and 30
		return ThreadLocalRandom.current().nextInt(0, 30);
	}
	
	private void sendChangesToAll (String changes) {
		ArrayList<InetAddress> peers = pl.getListOfAllPeers();
		if (peers.size() > 0) {
			System.out.println("Sending heartbeat: " + changes);
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
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, peerIP, 8585);
			socket.send(sendPacket);
			System.out.println(".....to " + peerIP.getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
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
			if(!upList.get(i).equals(IP)){
				packet += delim + upList.get(i).getHostAddress();
				delim = ",";
			}
		}
		packet += ";;";
		
		System.out.println("Sending full list heartbeat: " + packet);
		
		sendMessage = packet.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, IP, 8585);
		try {
			socket.send(sendPacket);
			System.out.println(".....to " + IP.getHostAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
