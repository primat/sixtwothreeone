package ca.primat.comp6231;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Date;

public class BankServer implements BankServerCustomerInterface, BankServerManagerInterface {
//public class BankServer implements BankServerInterface {
	
	protected LocalRegistry reg;
	protected Bank bank;

	/**
	 * Constructor - Use inversion of control so that we manage creation of dependencies outside this class
	 * 
	 * @param registry
	 * @param bank
	 */
	public BankServer(LocalRegistry reg, Bank bank) {
		super();
		this.reg = reg;
		this.bank = bank;
		try {
			this.reg.exportAndBind(this);
			
		} catch (RemoteException e) {
			System.out.println("Error: Remote exception while exporting and binding the bank server.");
			e.printStackTrace();
			System.exit(1);
		} catch (AlreadyBoundException e) {
			System.out.println("Error: Trying to bind server " + this.getId() + " to the registry but it is already bound.");
			//e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Get a lower case version of the bank name
	 * @return
	 */
	public String getId() {
		return bank.getName().toLowerCase();
	}
	
	// Operations performed by Customers
	@Override
	public Boolean openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
		
		
//		String password, double creditLimit) {
//		
//		// If the account exists, it will be overwritten (hint: Check if the account exists already)
//		
//		String firstChar = emailAddress.substring(0, 1);
//		
//		HashMap<String, Account> accountList = accounts.get(firstChar);
//		
//		if (accountList == null) {
//			accountList = new HashMap<String, Account>();
//		}
//		
//		if (accountList == null || !accountList.containsKey(emailAddress)) {
//			Account account = new Account(accountNbr, firstName, lastName, emailAddress, phoneNbr, password, creditLimit);
//			accountList.put(emailAddress, account);
//			this.accounts.put(firstChar, accountList);
//			return new AccountValidationResponse(true, "");
//		}
//		
//		
//		
//		
//		return account;
		
		return false;
	}


	@Override
	public Boolean delayPayment(int loanId, Date currentDueDate, Date NewDueDate) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String printCustomerInfo() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getLoan(int accountNumber, String password, double loanAmount) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}