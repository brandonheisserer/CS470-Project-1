package edu.truman.spicegURLs.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.Timer;

/**
 * This class is a timer associated with a particular IP which will timeout an
 * IP from our list if a heart beat is not received for 30 seconds. A timeout
 * entails this object removing the IP from the up list, removing itself from
 * the favorites list, and killing itself.
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 7 March 2018
 */
public class IPTimer extends Thread {
	private Stack<InetAddress> receiver;
	private InetAddress myAddress;
	private Timer time;
	private PeerList peers;
	private ArrayList<IPTimer> timers;
	
	/**
	 * Creates an instance of the object IPTimer and initializes all local variables.
	 * @param address InetAddress of the IP you want to listen for
	 * @param peers a reference to the peerList so it can remove said IP on time out
	 * @param timers a reference to the list of timers you are storing this in so
	 * on time out it can remove itself from said array list
	 * @return IPTimer object
	 */
	public IPTimer (InetAddress address, PeerList peers, ArrayList<IPTimer> timers){
		super(address.getHostAddress());
		receiver = new Stack<InetAddress>();
		myAddress = address;
		this.timers = timers;
		this.peers = peers;
		
		time = new Timer(30000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				timeout();
			}
		});
	}
	
	/**
	 * Overrides Thread's run method to check if the IP is heard.
	 * If it is heard its timer is reset.
	 */
	@Override
	public void run () {
		time.start();
		while (true) {
			if (!receiver.isEmpty()) {
				if (receiver.peek().equals(myAddress)) {
					time.restart();
					if (Globals.verbose) {
						System.out.println(".....restarted their timer!");
					}
				}
				receiver.pop();
			}
			try {
				Thread.sleep(10);
			}
			catch(Exception e) {
			}
		}
		
	}
	
	/**
	 * Enters passed InetAddress into buffer to be checked if the IP
	 * matching the timer can be reset. This is to make sure it does
	 * not time-out.
	 * @param IP the address to add to buffer
	 */
	public void sendIP (InetAddress address) {
		receiver.push(address);
	}
	
	/**
	 * Tells if the InetAddress entered is associated with myAddress
	 * @param address the IP to compare with myAddress
	 * @return boolean describing if two addresses are the same
	 */
	public boolean isIP (InetAddress address) {
		return this.myAddress.equals(address);
	}
	
	/**
	 * Tells the peer list to drop the associated IP and kills the thread.
	 * To be called when node times out.
	 */
	private void timeout () {
		if (Globals.verbose) {
			System.out.print("IPTimer: " + myAddress.getHostAddress());
			System.out.println(" timed out, putting them on down list");
		}
		peers.dropPeer(myAddress);
		timers.remove(this);
		time.stop();
		this.interrupt();
	}
}