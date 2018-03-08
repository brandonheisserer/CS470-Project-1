package edu.truman.spicegURLs.node;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Waits to revceive heartbeats from any other peers. Further
 * documentation can be found in the P2P version, to which
 * this is nearly identical.
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 7 March 2018
 */
public class Listener implements Runnable {

	DatagramSocket socket = null;
	private Messenger mess;
	private PeerList pl;
	private ArrayList<IPTimer> favorites;
	private boolean isServer;
	
	public Listener (PeerList pl, Messenger mess, boolean isServer) {
		this.pl = pl;
		this.mess = mess;
		this.isServer = isServer;
		favorites = new ArrayList<IPTimer>();
	}
	
	public void parsePacket (String message) {
		String[] packet = message.split(";");
		String version = packet[0];
		String[] upList = packet[1].split(",");
		String[] downList = packet[2].split(",");
		
		if (!version.equals("1.0")) {
			System.err.println("Received bad packet version: " + version);
			return;
		}
		
		if (upList.length == 1 && upList[0].isEmpty()) {
			upList = new String[0];
		}
		
		if (downList.length == 1 && downList[0].isEmpty()) {
			downList = new String[0];
		}
		
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
            socket = new DatagramSocket(8585);
            while (true) 
            {
            	byte[] incomingData = new byte[1024];
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, 
                		incomingData.length);
                
                // wait here for new data
                socket.receive(incomingPacket);
                
                String message = new String(incomingPacket.getData());
                InetAddress ipOfSender = incomingPacket.getAddress();
                
                // make sure the sender is on our up list
                pl.addPeer(ipOfSender);
                
                if (Globals.verbose) {
                	System.out.println("Received heartbeat: " + message);
                	System.out.println(".....from: " + ipOfSender.getHostAddress());
                }
                
                /* The following if-statement is specific to ClientServer and its
                 * function is ensure only the server can time clients out. */
                if(isServer){
	                if(!isIPinOurFavorites(ipOfSender)){
	                	if (Globals.verbose) {
	                		System.out.println(".....who is NOT on our favorites.");
	                	}
	                	addIPTimer(ipOfSender);
	                	mess.sendListforJoin(ipOfSender);
	                	if (Globals.verbose) {
	                		System.out.println(".....added a timer for them, and send them entire list.");
	                	}
	                } else {
	                	if (Globals.verbose) {
	                		System.out.println(".....who is on our favorites.");
	                	}
	                }
                }
                
                // adds the ip of sender to all favorites' stacks
                for (int i = 0; i < favorites.size(); i++) {
                	favorites.get(i).SendIP(ipOfSender);
                }
                
                // act upon the contents of the message
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
	
	private void addIPTimer(InetAddress IP){
		IPTimer timer = new IPTimer(IP,pl,favorites);
		favorites.add(timer);
		timer.start();
	}
	
	private boolean isIPinOurFavorites(InetAddress IPARG){
		for (int i = 0; i<favorites.size();i++) {
			if (favorites.get(i).isIP(IPARG)) {
				return true;
			}
		}
		return false;
	}
}
