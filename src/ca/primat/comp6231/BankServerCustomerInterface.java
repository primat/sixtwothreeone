package ca.primat.comp6231;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankServerCustomerInterface extends Remote {

	// Operations performed by Customers
	public Boolean openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws RemoteException;
	public Boolean getLoan(int accountNumber, String password, double loanAmount) throws RemoteException;
	
}
