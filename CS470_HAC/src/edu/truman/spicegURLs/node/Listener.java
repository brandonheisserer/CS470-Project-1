package edu.truman.spicegURLs.node;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Listener implements Runnable {

	DatagramSocket socket = null;
	private PeerList pl;
	private ArrayList<IPTimer> IPTimers;
	public Listener (PeerList pl) {
		this.pl = pl;
		IPTimers = new ArrayList<IPTimer>();
	}
	
	private void onReceive (String message) {

        ArrayList<String> changes = this.parseChanges(message);
        
        for (String change : changes) {
        	InetAddress peerIP;
			try {
				peerIP = InetAddress.getByName(change.substring(1));
				if (change.startsWith("U")) {
					pl.addPeer(peerIP);
				} else if (change.startsWith("D")) {
					pl.dropPeer(peerIP);
				} else {
					System.out.println("Bad change code: " + change);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private ArrayList<String> parseChanges(String message) {
		return new ArrayList<String>(Arrays.asList(message.split(";")));
	}
	
	@Override
	public void run()
    {
        try 
        {
            socket = new DatagramSocket(9876);
            byte[] incomingData = new byte[1024];

            while (true) 
            {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, 
                		incomingData.length);
                
                // wait here for new data
                socket.receive(incomingPacket);
                
                String message = new String(incomingPacket.getData());
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                for(int i = 0; i < IPTimers.size(); i++){
                	IPTimers.get(i).SendIP(IPAddress);
                }
                System.out.println("Received message from client: " + message);
                System.out.println("Client IP:" + IPAddress.getHostAddress());
                System.out.println("Client port:" + port);
                
                
                this.onReceive(message);
            }
        } 
        catch (SocketException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException i) 
        {
            i.printStackTrace();
        }
    }
}
