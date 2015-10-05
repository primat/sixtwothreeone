package ca.primat.comp6231;

import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * The customer client application
 * 
 * @author mat
 *
 */
public class CustomerClient extends Client<BankServerCustomerInterface> {

	/**
	 * Constructor
	 */
	public CustomerClient() {
		super();
	}
	
	public static void showMenu()
	{
		System.out.println("\n****Welcome to TextScrambler****\n");
		System.out.println("Please select an option (1-4)");
		System.out.println("1. Test sample input.");
		System.out.println("2. Reverse input");
		System.out.println("3. Scramble input");
		System.out.println("4. Exit");
	}
	
	/**
	 * Entry point of the customer application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new CustomerClient();
		System.out.println("Client connected succesfully");
		
		int userChoice = 0;
		String userInput = "";
		String requestInput = "Please enter a random string.";
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
				//System.out.println(server.testInputText(userInput));
				showMenu();
				break;
			case 2:
				System.out.println(requestInput);
				userInput=keyboard.next();
				//System.out.println(server.reverse(userInput));
				showMenu();
				break;
			case 3:
				System.out.println(requestInput);
				userInput=keyboard.next();
				//System.out.println(server.scramble(userInput));
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
	}
	
	/**
	 * 
	 * @param bank
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param password
	 * @return
	 */
	public Boolean openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
		
		BankServerCustomerInterface server = this.getBankServer(bank);
		try {
			server.openAccount(firstName, lastName, emailAddress, phoneNumber, password);
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not open account");
			//e.printStackTrace();
		}	
		return null;
	}

	/**
	 * 
	 * @param bank
	 * @param accountNumber
	 * @param password
	 * @param loanAmount
	 * @return
	 */
	public Boolean getLoan(String bank, int accountNumber, String password, double loanAmount) {
		
		BankServerCustomerInterface server = this.getBankServer(bank);
		try {
			server.getLoan(accountNumber, password, loanAmount);
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not open account");
			//e.printStackTrace();
		}	
		return null;
	}
	
}
