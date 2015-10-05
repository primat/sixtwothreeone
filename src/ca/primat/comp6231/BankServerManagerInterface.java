package ca.primat.comp6231;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface BankServerManagerInterface extends Remote {

	// Operations performed by Managers
	public Boolean delayPayment (int loanId, Date currentDueDate, Date NewDueDate) throws RemoteException;
	public String printCustomerInfo() throws RemoteException;

}
