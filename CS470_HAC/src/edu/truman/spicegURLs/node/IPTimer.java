package edu.truman.spicegURLs.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.Stack;

import javax.swing.Timer;

public class IPTimer extends Thread {
	private Stack<InetAddress> receiver;
	private InetAddress IP;
	private Timer time;
	private PeerList pr;
	
	/*Creates and instance of the object IPTimer and initializes all local variables
	 * @param InetAddress of the IP you want to listen for, PeerList, a refference to your peerlist
	 * @return IPTimer object
	 */
	public IPTimer(InetAddress IPARG, PeerList prarg){
		super(IPARG.getHostAddress());
		receiver = new Stack<InetAddress>();
		IP = IPARG;
		time = new Timer(5000, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timeout();
			}
			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		time.start();
		while(true){
			if(!receiver.isEmpty()){
				if(receiver.peek().getHostAddress().equals(IP.getHostAddress())){
					time.restart();
				}
				receiver.pop();
			}
			try{
				Thread.sleep(100);
			}
			catch(Exception e){
				System.err.println("Thread Timer: " +IP+" sleep failed");
				System.err.println(e);
			}
		}
		
	}
	/*
	 * gives an InetAddress to the buffer to check for time out
	 */
	public void SendIP(InetAddress IPARG){
		receiver.push(IPARG);
	}
	/*
	 * Calls when node times out, tells the peer list to drop the associated IP and kills the thread
	 */
	private void timeout(){
		pr.dropPeer(IP);
		Thread.currentThread().interrupt();
	}

}
