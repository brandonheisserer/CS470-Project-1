package main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import edu.truman.spicegURLs.node.*;

/**
 * Serves to get the system started.
 * @author Brandon Crane
 * @author Brandon Heisserer
 * @author Tanner Krewson
 * @author Carl Yarwood
 * @version 7 March 2018
 */
public class Main {
	/**
	 * Requires the first node to wait for others to join, whereas
	 * joining nodes must enter the IP address of a connected node.
	 * Will also change what messages appear to the usr based on the
	 * user's preference.
	 * @param args not used
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to the Spice gUrls HAC!");
		System.out.println("Would you like debug comments? (Y/N): ");
		String debug = sc.nextLine();
		if (debug.equals("Y") || debug.equals("y")) {
			Globals.verbose = true;
			System.out.println("Debug comments will be included.\n");
		} else {
			Globals.verbose = false;
			System.out.println("Debug comments will be excluded.\n");
		}
		
		while (true) {
			System.out.println("1 - Inital Node");
			System.out.println("2 - Connect to a node");
			System.out.println("Enter a mode:");
			int i = sc.nextInt();
			sc.nextLine();
			Messenger messenger;
			
			if (i == 1) {
				messenger = new Messenger();
				messenger.waitToSendNextHeartbeat();
				try {
					messenger.listener.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			} else if (i == 2) {
				InetAddress ip;
				while (true) {
					try {
						System.out.println("Enter an IP to connect to: ");
						ip = InetAddress.getByName(sc.nextLine());
						break;
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
				System.out.println("IP " + ip + " entered, starting..." );
				messenger = new Messenger();
				messenger.addInitialPeer(ip);
				messenger.waitToSendNextHeartbeat();
				try {
					messenger.listener.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
