package main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import edu.truman.spicegURLs.node.*;

public class Main {

	public static void main(String[] args) {
		System.out.println("Welcome to the Spice gUrls HAC!");
		while (true) {
			System.out.println("Enter a mode:");
			System.out.println("1 - Inital Node");
			System.out.println("2 - Connect to a node");
			Scanner sc = new Scanner(System.in);
			int i = sc.nextInt();
			if (i == 1) {
				Messenger messenger = new Messenger(); // this also triggers the listener thread
				messenger.waitToSendNextHeartbeat();
				break;
			} else if (i == 2) {
				InetAddress ip;
				while (true) {
					try {
						System.out.println("Enter an IP to connect to: ");
						ip = InetAddress.getByName(sc.nextLine());
						break;
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	
				Messenger messenger = new Messenger(); // this also triggers the listener thread
				messenger.addInitialPeer(ip);
				messenger.waitToSendNextHeartbeat();
				break;
			}

		}
		System.out.println("End of main");
	}

}
