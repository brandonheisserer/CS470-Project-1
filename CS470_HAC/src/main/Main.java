package main;

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
				Listener testListener = new Listener();
				testListener.createAndListenSocket();
				break;
			} else if (i == 2) {
				Messenger testMessenger = new Messenger();
				testMessenger.createAndListenSocket();
				break;
			}

		}
		System.out.println("End of main");
	}

}
