package ca.primat.comp6231;
/**
 * @author Alexandre Hudon
 * @date 18/09/2013
 * RMI-Tutorial, COMP 6231 - Text Scrambler Client
 * This class needs to be modified by the students in order to define it as a Java RMI Client.
 * Enables a user to send in a sentence to the server and select 4 different options: 3 to
 * process the sentence, 1 to exit the server.
 */

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Scanner;

public class TextScramblerClient {
	
	public static final String REGISTRY_URL = "rmi://localhost:2020/test";
	
	//Return basic menu.
	public static void showMenu()
	{
		System.out.println("\n****Welcome to TextScrambler****\n");
		System.out.println("Please select an option (1-4)");
		System.out.println("1. Test sample input.");
		System.out.println("2. Reverse input");
		System.out.println("3. Scramble input");
		System.out.println("4. Exit");
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		try {
			System.setSecurityManager(new RMISecurityManager());
		
			//Create an instance of the server -- to be replaced with RMIRegistry lookup.
			//TextScramblerServer server = new TextScramblerServer();
			TextScramblerInterface server = (TextScramblerInterface) Naming.lookup(REGISTRY_URL);
			
			int userChoice=0;
			String userInput="";
			String requestInput= "Please enter a random string.";
			Scanner keyboard = new Scanner(System.in);
			
			showMenu();
			
			while(true)
			{
				Boolean valid = false;
				
				// Enforces a valid integer input.
				while(!valid)
				{
					try{
						userChoice=keyboard.nextInt();
						valid=true;
					}
					catch(Exception e)
					{
						System.out.println("Invalid input. Please enter an integer.");
						valid=false;
						keyboard.nextLine();
					}
				}
				
				// Manage user selection.
				switch(userChoice)
				{
				case 1: 
					System.out.println(requestInput);
					userInput=keyboard.next();
					System.out.println(server.testInputText(userInput));
					showMenu();
					break;
				case 2:
					System.out.println(requestInput);
					userInput=keyboard.next();
					System.out.println(server.reverse(userInput));
					showMenu();
					break;
				case 3:
					System.out.println(requestInput);
					userInput=keyboard.next();
					System.out.println(server.scramble(userInput));
					showMenu();
					break;
				case 4:
					System.out.println("Have a nice day!");
					keyboard.close();
					System.exit(0);
				default:
					System.out.println("Invalid input. Please try again.");
				}
			}
		} catch (RemoteException e1) {
			//e1.printStackTrace();
			System.out.println("Fatal error: RemoteException when connecting to " + REGISTRY_URL);
		} catch (MalformedURLException e1) {
			//e1.printStackTrace();
			System.out.println("Fatal error: Maleformed URL in " + REGISTRY_URL);
		} catch (NotBoundException e1) {
			//e1.printStackTrace();
			System.out.println("Fatal error: Unable to bind to " + REGISTRY_URL);
			System.exit(0);
		}
	}
}
