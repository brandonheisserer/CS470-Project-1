package edu.truman.spicegURLs.node;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Listener implements Runnable {

	DatagramSocket socket = null;
	private Messenger mess;
	private PeerList pl;
	private ArrayList<IPTimer> favorites;
	public Listener (PeerList pl, Messenger mess) {
		this.pl = pl;
		this.mess = mess;
		favorites = new ArrayList<IPTimer>();
	}
	
	public void parsePacket (String message) {
		String[] packet = message.split(";");
		String version = packet[0];
		String[] upList = packet[1].split(",");
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
                pl.addPeer(IPAddress);
                int port = incomingPacket.getPort();
                if(!checkIP(IPAddress)){
                	addIPTimer(IPAddress);
                	mess.sendListforJoin(IPAddress);
                }
                for(int i = 0; i < favorites.size(); i++){
                	favorites.get(i).SendIP(IPAddress);
                }
                System.out.println("Received hearbeat: " + message);
                System.out.println(".....from:" + IPAddress.getHostAddress());
                
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
		favorites.add(new IPTimer(IP,pl,favorites));
	}
	private boolean checkIP(InetAddress IPARG){
		for(int i = 0; i<favorites.size();i++){
			if(favorites.get(i).isIP(IPARG)){
				return true;
			}
		}
		return false;
	}
}
