package ca.primat.comp6231;

import java.rmi.RemoteException;
import java.util.Date;

/**
 * The manager client application
 * 
 * @author mat
 * 
 */
public class ManagerClient extends Client<BankServerManagerInterface> {

	/**
	 * Constructor
	 */
	public ManagerClient() {
		super();
	}
	
	/**
	 * Entry point of the manager application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new ManagerClient();
	}
	
	public Boolean delayPayment (String bank, int loanId, Date currentDueDate, Date NewDueDate) {
		
		BankServerManagerInterface server = this.getBankServer(bank);
		try {
			server.delayPayment(loanId, currentDueDate, NewDueDate);
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not delay payment");
			//e.printStackTrace();
		}	
		return null;
	}
	
	public String printCustomerInfo(String bank) {
		BankServerManagerInterface server = this.getBankServer(bank);
		try {
			server.printCustomerInfo();
		} catch (RemoteException e) {
			System.out.println("Remote exception: could not print customer info");
			//e.printStackTrace();
		}	
		return null;
	}
	
}
