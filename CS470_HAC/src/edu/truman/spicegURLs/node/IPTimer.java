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
	
	public IPTimer(InetAddress IPARG, PeerList prarg){
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
				System.err.println("Thread Timer: " +IP+" Died");
				System.err.println(e);
			}
		}
		
	}
	public void SendIP(InetAddress IPARG){
		receiver.push(IPARG);
	}
	private void timeout(){
		pr.dropPeer(IP);
		Thread.currentThread().interrupt();
	}

}
