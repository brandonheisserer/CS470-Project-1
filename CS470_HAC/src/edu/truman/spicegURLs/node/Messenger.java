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
	
	public void addInitialPeer(InetAddress ip) {
		pl.addInitialPeer(ip);
	}
	
	public void waitToSendNextHeartbeat () {
		
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  // pull info from heartbeat buffer
				  String changes = hbb.getChanges();
				  
				  // send data to all peers on list
				  Messenger.this.sendChangesToAll(changes);
				  
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
		ArrayList<InetAddress> peers = pl.getPeerList();
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
	
    /*public void createAndListenSocket() 
    {
        try 
        {
            InetAddress IPAddress = InetAddress.getByName("150.243.147.215");
            byte[] incomingData = new byte[1024];
            String sentence = "Tell me what you want";
            byte[] data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
            socket.send(sendPacket);
            System.out.println("Message sent from client");
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            socket.receive(incomingPacket);
            String response = new String(incomingPacket.getData());
            System.out.println("Response from server:" + response);
            socket.close();
        }
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }*/
}
