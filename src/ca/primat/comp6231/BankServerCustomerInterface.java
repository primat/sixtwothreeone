package ca.primat.comp6231;

import java.rmi.Remote;
import java.rmi.RemoteException;
import ca.primat.comp6231.response.GetLoanResponse;
import ca.primat.comp6231.response.OpenAccountResponse;

public interface BankServerCustomerInterface extends Remote {

	// Operations performed by Customers
	public OpenAccountResponse openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws RemoteException;
	public GetLoanResponse getLoan(int accountNbr, String password, int loanAmount) throws RemoteException;
}
