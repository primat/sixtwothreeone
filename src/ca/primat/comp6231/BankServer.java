package ca.primat.comp6231;

import java.net.InetSocketAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ca.primat.comp6231.response.GetLoanResponse;
import ca.primat.comp6231.response.OpenAccountResponse;
import ca.primat.comp6231.response.ServerResponse;

/**
 * BankServer is the object which handles server aspects of operations on Banks
 * 
 * @author mat
 *
 */
public class BankServer implements BankServerCustomerInterface, BankServerManagerInterface {

	protected LocalRegistry reg;
	protected Bank bank;
	protected HashMap<String, Bank> bankCollection;
	protected int newAccountNbr = 111111;

	/**
	 * Constructor - Use inversion of control so that we manage creation of dependencies outside this class
	 * 
	 * @param reg The local registry where BankServers register to make themselves available to clients
	 * @param bankCollection The collection of all banks available in the system
	 * @param bankId The bank ID of the bank that this server is managing
	 */
	public BankServer(LocalRegistry reg, HashMap<String, Bank> bankCollection, String bankId) {
		
		super();
		this.reg = reg;
		this.bankCollection = bankCollection;
		this.bank = bankCollection.get(bankId);
		
		try {
			this.reg.exportAndBind(this);
		} catch (RemoteException e) {
			System.out.println("Error: Remote exception while exporting and binding the bank server.");
			e.printStackTrace();
			System.exit(1);
		} catch (AlreadyBoundException e) {
			System.out.println("Error: Trying to bind server " + this.bank.getId() + " to the registry but it is already bound.");
			//e.printStackTrace();
			//System.exit(1);
		}

//		Executor executor = Executors.newSingleThreadExecutor();
//		executor.execute(new Runnable() { 
//			public void run() {
//				Set<Integer> peerPorts = new HashSet<Integer>();
//				peerPorts.add(10102);
//				peerPorts.add(10103);
//				BankUdpPeer udpListener = new BankUdpPeer(10101, peerPorts); 
//			} 
//		});
		
//		final Thread t1 = new Thread() {
//			@Override
//			public void run() {
//				UDPDataExchange udpListener = new UDPDataExchange();
//			}
//		};
//		t1.start();
		
	}
	 
	//
	// Operation performed by Customers
	//

	@Override
	public GetLoanResponse getLoan(int accountNbr, String password, int loanAmount) throws RemoteException {

		// Validate that the account exists
		Account account = this.bank.authenticateAccount(accountNbr, password);
		if (account == null) {
			return new GetLoanResponse(false, "", "Account does not exist", 0);
		}

		// Validate that passwords match
		if (account.getPassword() != password) {
			return new GetLoanResponse(false, "", "Invalid credentials", 0);
		}
		
		// Validate the loan request
		// Get the loan sum for all banks and approve or not the new loan
		

		return new GetLoanResponse(true, "", "Loan approved", 0);
	}
	
	@Override
	public OpenAccountResponse openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) {

		// Perform some field validation
		
		// Check if there is already an account with that username/email address
		if (this.bank.accountExists(emailAddress) ) {
			return new OpenAccountResponse(false, "", "The account " + emailAddress + " already exists", 0);
		}

		// TODO: more validation required
		
		// Create the account
		int newAccountNbr = this.newAccountNbr;
		Account newAccount = new Account(this.newAccountNbr++, firstName, lastName, emailAddress, phoneNumber, password);
		
		// Get the list of accounts by first letter, or create it if it doesn't exist
		String firstLetter = emailAddress.substring(0, 1);
		ThreadSafeHashMap<String, Account> accountsByLetter = this.bank.accounts.get(firstLetter);
		if (accountsByLetter == null) {
			accountsByLetter = new ThreadSafeHashMap<String, Account>();
			// Add the accounts list to the list of accounts by first letter
			this.bank.accounts.put(firstLetter, accountsByLetter);
		}
		
		// Add the newly created account to the list
		accountsByLetter.put(emailAddress, newAccount);

		return new OpenAccountResponse(true, "", "Account " + emailAddress + "successfully create", newAccountNbr);
	}

	//
	// Operation performed by Managers
	//
	
	@Override
	public ServerResponse delayPayment(int loanId, Date currentDueDate, Date newDueDate) throws RemoteException {

		Loan loan = this.bank.getLoanById(loanId);
		if (loan == null) {
			return new ServerResponse(false, "", "Loan id " + loanId + " does not exist");
		}
		if (!loan.dueDate.equals(currentDueDate)) {
			return new ServerResponse(false, "", "Loan id " + loanId + " - currentDate argument mismatch");
		}
		if (!loan.dueDate.before(currentDueDate)) {
			return new ServerResponse(false, "", "Loan id " + loanId + " - currentDueDate argument must be later than the actual current due date of the loan");
		}
		
		loan.setDueDate(newDueDate);
		
		return new ServerResponse(true, "", "Loan successfully delayed");
	}

	@Override
	public String printCustomerInfo() throws RemoteException {
		
		for (String key : this.bank.accounts.keySet()) {
			ThreadSafeHashMap<String, Account> accountsByLetter = this.bank.accounts.get(key);
			for (String accountId : accountsByLetter.keySet()) {
				Account account = accountsByLetter.get(accountId);
				System.out.println(accountId + ": " + account.toString());
				System.out.println("------------------------------------");
			}
		}
		
		return null;
	}
	
	/**
	 * Get the addresses of banks other than this one
	 * @return
	 */
	protected Set<InetSocketAddress> getPeerAddresses() {
		
		Set<InetSocketAddress> peerAddresses = new HashSet<InetSocketAddress>();
		
		for(Bank bankObj : bankCollection.values()) {
		    if (bankObj.udpAddress != this.bank.udpAddress) {
		    	peerAddresses.add(bankObj.udpAddress);
		    }
		}
		
		return peerAddresses;
	}

}
