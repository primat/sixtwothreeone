package ca.primat.comp6231;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import ca.primat.comp6231.response.GetLoanResponse;
import ca.primat.comp6231.response.OpenAccountResponse;

/**
 * The customer client application
 * 
 * @author mat
 *
 */
public class CustomerClient extends Client<BankServerCustomerInterface> {

	protected static int instances = 0;
	final protected int id;
	protected Logger logger = null;
	
	/**
	 * Constructor
	 */
	public CustomerClient() {
		super();
		this.id = ++instances;

		// Set up the logger
		String textId = "CustomerClient" + this.id;
		this.logger = Logger.getLogger(textId);
	    FileHandler fh;  

	    try {
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(textId + "-log.txt");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.info(textId + " logger started");
	    } catch (SecurityException e) {  
	        e.printStackTrace();
	        System.exit(1);
	    } catch (IOException e) {  
	        e.printStackTrace(); 
	        System.exit(1); 
	    }  
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
				//System.out.println(server.testInputText(userInput));
				showMenu();
				break;
			case 2:
				System.out.println(requestInput);
				//System.out.println(server.reverse(userInput));
				showMenu();
				break;
			case 3:
				System.out.println(requestInput);
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
	public int openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
		
		BankServerCustomerInterface server = this.getBankServer(bank);
		try {
			System.out.println("Opening an account at " + bank + " for user " + emailAddress);
			OpenAccountResponse response = server.openAccount(firstName, lastName, emailAddress, phoneNumber, password);
			if (response.accountNbr > 0) {
				System.out.println("Account " + emailAddress + " added successfully at bank " + bank);
			}
			else {
				System.out.println("Could not open account " + emailAddress + " at bank " + bank);
			}
			return response.accountNbr;
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not open account. Please try again later");
			//e.printStackTrace();
		}	
		return 0;
	}
	
	/**
	 * Request a loan at the given Bank
	 * 
	 * @param bank
	 * @param accountNumber
	 * @param password
	 * @param loanAmount
	 * @return
	 */
	public GetLoanResponse getLoan(String bank, int accountNumber, String password, int loanAmount) {
		
		BankServerCustomerInterface server = this.getBankServer(bank);
		try {
			GetLoanResponse response = server.getLoan(accountNumber, password, loanAmount);
			if (response.newLoanId > 0) {
				System.out.println("Account " + accountNumber + " successfully got a loan of " + loanAmount + " at bank " + bank);
			}
			else {
				System.out.println("Account " + accountNumber + " was refused a loan of " + loanAmount + " at bank " + bank);
			}
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not get a loan. Please try again later");
			//e.printStackTrace();
		}	
		return null;
	}
}
