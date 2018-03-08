package edu.truman.spicegURLs.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.Timer;

/**
 * This class is a timer which is associated with a particular IP which
 * will time out an IP from our list if a heartbeat is not received for
 * 30 seconds. A timeout entails this object removing the IP from the up
 * list, removing itself from the favorites list and killing itself.
 * Further documentation can be found in the P2P version, to which this
 * is nearly identical.
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
	private PeerList pr;
	private ArrayList<IPTimer> list;
	
	public IPTimer (InetAddress IPARG, PeerList prarg, ArrayList<IPTimer> listarg) {
		super(IPARG.getHostAddress());
		receiver = new Stack<InetAddress>();
		IP = IPARG;
		list = listarg;
		pr = prarg;
		time = new Timer(30000, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				timeout();
			}
		});
	}
	
	@Override
	public void run() {
		time.start();
		while (true) {
			if(!receiver.isEmpty()){
				if(receiver.peek().equals(IP)){
					if (Globals.verbose) {
						System.out.println(".....restarted their timer!");
					}
					time.restart();
				}
				receiver.pop();
			}
			try{
				Thread.sleep(10);
			}
			catch(Exception e){
			}
		}
	}
	
	public void SendIP (InetAddress IPARG) {
		receiver.push(IPARG);
	}
	
	public boolean isIP (InetAddress IPARG) {
		return IP.equals(IPARG);
	}
	
	private void timeout() {
		if (Globals.verbose) {
			System.out.println("IPTimer: " + IP.getHostAddress() + " timed out, putting them on down list");
		}
		pr.dropPeer(IP);
		list.remove(this);
		time.stop();
		this.interrupt();
	}

}