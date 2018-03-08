package edu.truman.spicegURLs.node;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Waits to receive heartbeats from any other peers.
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
	
	/**
	 * Creates an instance of the object Listener and initializes
	 * all local variables.
	 * @param pl reference to the peer list
	 * @param mess reference to the messenger
	 * @return Listener object
	 */
	public Listener (PeerList pl, Messenger mess) {
		this.pl = pl;
		this.mess = mess;
		favorites = new ArrayList<IPTimer>();
	}
	
	/**
	 * Takes the payload from received packets, parses out 
	 * the newly up and down nodes, and puts them into the
	 * peer list.
	 * @param message the payload from the packet
	 */
	public void parsePacket (String message) {
		String[] packet = message.split(";");
		//String version = packet[0];
		String[] upList = packet[1].split(",");
		String[] downList = packet[2].split(",");
		
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
	
	/**
	 * Waits for new packets, receives them, and processes them.
	 */
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
                
                // if this peer is not in our favorites, add a drop timer,
                // and send them the full list
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
                
                // adds the ip of sender to all favorites' stacks
                for(int i = 0; i < favorites.size(); i++){
                	favorites.get(i).sendIP(ipOfSender);
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
	
	/**
	 * Sets a timer to make sure the given IP is still alive
	 * @param IP
	 */
	private void addIPTimer(InetAddress IP){
		IPTimer timer = new IPTimer(IP,pl,favorites);
		favorites.add(timer);
		timer.start();
	}
	
	/**
	 * Checks to see if the given IP is in the list of favorites
	 * @param IPARG any ip address
	 * @return true if the ip is in favorites
	 */
	private boolean isIPinOurFavorites(InetAddress IPARG){
		for(int i = 0; i<favorites.size();i++){
			if(favorites.get(i).isIP(IPARG)){
				return true;
			}
		}
		return false;
	}
}
