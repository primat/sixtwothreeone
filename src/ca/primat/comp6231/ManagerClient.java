package ca.primat.comp6231;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import ca.primat.comp6231.response.ServerResponse;

/**
 * The manager client application
 * 
 * @author mat
 * 
 */
public class ManagerClient extends Client<BankServerManagerInterface> {

	protected static int instances = 0;
	final protected int id;
	
	/**
	 * Constructor
	 */
	public ManagerClient() {
		
		super();
		this.id = ++instances;
		
		// Set up the logger
		String textId = "ManagerClient" + this.id;
		this.logger = Logger.getLogger(textId);
	    FileHandler fh;  

	    try {
	        // This block configures the logger with handler and formatter  
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
	
	/**
	 * Entry point of the manager application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new ManagerClient();
	}
	
	/**
	 * 
	 * @param bank
	 * @param loanId
	 * @param currentDueDate
	 * @param NewDueDate
	 * @return
	 */
	public Boolean delayPayment (String bank, int loanId, Date currentDueDate, Date NewDueDate) {
		
		BankServerManagerInterface server = this.getBankServer(bank);
		try {
			ServerResponse response = server.delayPayment(loanId, currentDueDate, NewDueDate);
			logger.info(this.getTextId() + ": " + response.message);
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not delay payment");
			logger.info(this.getTextId() + ": Remote exception: could not delay payment");
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * 
	 * @param bank
	 * @return
	 */
	public String printCustomerInfo(String bank) {
		
		BankServerManagerInterface server = this.getBankServer(bank);
		try {
			String result = server.printCustomerInfo();
			logger.info(this.getTextId() + ": Bank-" + bank + "\n" + result);
			
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not print customer info");
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getTextId() {
		return "ManagerClient-" + this.id;
	}
}
