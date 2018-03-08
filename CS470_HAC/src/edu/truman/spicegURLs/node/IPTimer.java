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
	private InetAddress IP;
	private Timer time;
	private PeerList peers;
	private ArrayList<IPTimer> timers;
	
	/**
	 * Creates an instance of the object IPTimer and initializes all local variables.
	 * @param newIP InetAddress of the IP you want to listen for
	 * @param peers a reference to the peerList so it can remove said IP on time out
	 * @param timers a reference to the list of timers you are storing this in so
	 * on time out it can remove itself from said array list
	 * @return IPTimer object
	 */
	public IPTimer (InetAddress newIP, PeerList peers, ArrayList<IPTimer> timers){
		super(newIP.getHostAddress());
		receiver = new Stack<InetAddress>();
		IP = newIP;
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
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run () {
		time.start();
		while (true) {
			if (!receiver.isEmpty()) {
				if (receiver.peek().equals(IP)) {
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
	 * Enters passed InetAddress into buffer to be checked so that when
	 * the if matching the timer can be reset
	 * @param InetAddress you would like to add to buffer to make sure it doesn't time out
	 */
	public void SendIP(InetAddress IPARG){
		receiver.push(IPARG);
	}
	
	/**
	 * Tells if entered InetAddress is associated with IPTimer
	 * @param Ip you would like to see if it is associated with timer
	 * @return if the parameter is the associated IP
	 */
	public boolean isIP(InetAddress IPARG){
		return IP.equals(IPARG);
	}
	
	/*
	 * Calls when node times out, tells the peer list to drop the associated IP and kills the thread
	 */
	private void timeout(){
		System.out.println("IPTimer: " + IP.getHostAddress() + " timed out, putting them on down list");
		peers.dropPeer(IP);
		timers.remove(this);
		time.stop();
		this.interrupt();
	}
}