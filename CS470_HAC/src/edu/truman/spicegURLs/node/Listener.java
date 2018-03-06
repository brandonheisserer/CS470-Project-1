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
	
	public void parsePacket (String message) {
		String[] packet = message.split(";");
		String version = packet[0];
		boolean join = packet[1] != "0";
		String[] upList = packet[2].split(",");
		String[] downList = packet[2].split(",");
		
		for (int i = 0; i < upList.length; i++) {
			InetAddress newNode;
			try {
				newNode = InetAddress.getByName(upList[i]);
				pl.addPeer(newNode);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < downList.length; i++) {
			InetAddress newNode;
			try {
				newNode = InetAddress.getByName(downList[i]);
				pl.dropPeer(newNode);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
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
                
                parsePacket(message);
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
