package main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import edu.truman.spicegURLs.node.*;

public class Main {

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
			System.out.println("Enter a mode:");
			System.out.println("1 - Server");
			System.out.println("2 - Client");
			int i = sc.nextInt();
			sc.nextLine();
			Messenger messenger;
			if (i == 1) {
				try {
					messenger = new Messenger(true, InetAddress.getLocalHost());
					messenger.run();
					messenger.listener.join();
				} catch (UnknownHostException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} // this also triggers the listener thread
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
				System.out.println("IP " + ip + " entered, starting..." );
				messenger = new Messenger(false,ip); // this also triggers the listener thread
				messenger.addInitialPeer(ip);
				messenger.initClientHeartbeat();
				messenger.clientHeartbeat();
				try {
					messenger.listener.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

		}
		System.out.println("End of main");
	}

}
