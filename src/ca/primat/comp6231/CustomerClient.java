package ca.primat.comp6231;

import java.io.IOException;
import java.rmi.RemoteException;
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

	protected static int instances = 1;
	final protected int id;
	
	/**
	 * Constructor
	 */
	public CustomerClient() {
		super();
		this.id = instances++;

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
	    
	    logger.info("Starting customer client #" + this.id);
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
		    logger.info(this.getTextId() + ": Opening an account at " + bank + " for user " + emailAddress);
			OpenAccountResponse response = server.openAccount(firstName, lastName, emailAddress, phoneNumber, password);
			if (response.accountNbr > 0) {
			    logger.info(this.getTextId() + ": Account " + emailAddress + " created successfully at bank " + bank + " with account number " + response.accountNbr);
			}
			else {
			    logger.info(this.getTextId() + ": Could not open account " + emailAddress + " at bank " + bank + ". " + response.message);
			}
			return response.accountNbr;
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not open account. Please try again later");
			e.printStackTrace();
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
			System.out.println(response);
			if (response.newLoanId > 0) {
			    logger.info(this.getTextId() + ": Account " + accountNumber + " successfully got a loan of " + loanAmount + " at bank " + bank + " with loanId " + response.newLoanId);
				return response;
			}
			else {
			    logger.info(this.getTextId() + ": Account " + accountNumber + " was refused a loan of " + loanAmount + " at bank " + bank);
			}
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not get a loan. Please try again later");
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getTextId() {
		
		return "CustomerClient-" + this.id;
	}
}
