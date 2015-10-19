package ca.primat.comp6231;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import ca.primat.comp6231.response.GetLoanResponse;
import ca.primat.comp6231.response.OpenAccountResponse;
import ca.primat.comp6231.response.ServerResponse;

/**
 * BankServer is the object which handles client and manager operations on Banks
 * 
 * @author mat
 * 
 */
public class BankServer implements BankServerCustomerInterface, BankServerManagerInterface {

	protected volatile Bank bank;
	protected volatile HashMap<String, Bank> bankCollection;
	protected volatile Object lockObject;
	protected int sequenceNbr = 1;
	protected Logger logger = null;
	
	/**
	 * Constructor - Use inversion of control so that we manage creation of dependencies outside this class
	 * 
	 * @param reg The local registry where BankServers register to make themselves available to clients
	 * @param bankCollection The collection of all banks available in the system
	 * @param bankId The bank ID of the bank that this server is managing
	 */
	public BankServer(HashMap<String, Bank> bankCollection, String bankId, final Object lockObject) {
		
		super();
		this.bankCollection = bankCollection;
		this.bank = bankCollection.get(bankId);
		this.lockObject = lockObject;

		// Set up the logger
		this.logger = Logger.getLogger(this.bank.getTextId());  
	    FileHandler fh;  
	    try {
	        fh = new FileHandler(this.bank.getTextId() + "-log.txt");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.info(this.bank.getTextId() + " logger started");  
	    } catch (SecurityException e) {  
	        e.printStackTrace();
	        System.exit(1);
	    } catch (IOException e) {  
	        e.printStackTrace(); 
	        System.exit(1); 
	    }

		BankUdpListener udpPeer = new BankUdpListener(this.bank, this.logger);
		Thread udpPeerThread = new Thread(udpPeer);
		udpPeerThread.start();
	}
	
	//
	// Operation performed by Customers
	//

	@Override
	public GetLoanResponse getLoan(int accountNbr, String password, int loanAmount) throws RemoteException {

		int newLoanId = 0;

		logger.info("-------------------------------");
		logger.info(this.bank.getTextId() + ": Client invoked getLoan(accountNbr:" + accountNbr + ", password:" + password + ", loanAmount:" + loanAmount + ")");
			
		synchronized (lockObject) {
			
			// Test the existence of the account
			Account account = this.bank.getAccount(accountNbr);
			if (account == null) {
				logger.info(this.bank.getTextId() + ": Account " + accountNbr + " does not exist at bank " + this.bank.getId());
				return new GetLoanResponse(false, "Account " + accountNbr + " does not exist at bank " + this.bank.getId(), "", 0);
			}

			// Validate that passwords match
			if (!account.password.equals(password)) {
				logger.info(this.bank.getTextId() + ": Invalid credentials. Loan refused at bank " + this.bank.getId() +  " " + account.password + "/" + password);
				return new GetLoanResponse(false, "Invalid credentials. Loan refused at bank " + this.bank.getId(), "", 0);
			}
	
			// Avoid making UDP requests if the loan amount is already bigger than the credit limit of the local account
			int currentLoanAmount = this.bank.getLoanSum(accountNbr);
			if (currentLoanAmount + loanAmount > account.getCreditLimit()) {
				logger.info(this.bank.getTextId() + ": Loan refused at bank " + this.bank.getId() + ". Local credit limit exceeded");
				return new GetLoanResponse(false, "Loan refused at bank " + this.bank.getId() + ". Local credit limit exceeded", "", 0);
			}
			
			// Get the loan sum for all banks and approve or not the new loan
			ExecutorService pool = Executors.newFixedThreadPool(this.bankCollection.size()-1);
		    Set<Future<LoanRequestStatus>> set = new HashSet<Future<LoanRequestStatus>>();
		    for (Bank destinationBank : this.bankCollection.values()) {
		    	
		    	if (this.bank != destinationBank) {
					Callable<LoanRequestStatus> callable = new UdpRequesterCallable(this.bank, destinationBank, account.emailAddress, this.sequenceNbr, this.logger);
					Future<LoanRequestStatus> future = pool.submit(callable);
					set.add(future);
				}
			}
	
			int extLoanSum = 0;
			for (Future<LoanRequestStatus> future : set) {
	
				try {
					LoanRequestStatus status = future.get();
					if (status == null) {
						logger.info(this.bank.getTextId() + ": Loan refused at bank " + this.bank.getId() + ". Unable to obtain a status for the original loan request.");
						return new GetLoanResponse(false, "Loan refused at bank " + this.bank.getId() + ". Unable to obtain a status for the original loan request.", "", 0);
						//continue;
					}
					else if (status.status == LoanRequestStatus.STATUS_SUCCESS) {
						extLoanSum += status.loanSum;
					}
					else {
						logger.info(this.bank.getTextId() + ": Loan refused at bank " + this.bank.getId() + ". " + status.message);
						return new GetLoanResponse(false, status.message, "", 0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.info(this.bank.getTextId() + ": Loan request failed for user " + account.emailAddress + ". InterruptedException");
					return new GetLoanResponse(false, "Bank " + this.bank.getId() + " loan request failed for user " + account.emailAddress + ". InterruptedException", "", 0);
				} catch (ExecutionException e) {
					e.printStackTrace();
					logger.info(this.bank.getTextId() + ": Loan request failed for user " + account.emailAddress + ". InterExecutionExceptionruptedException");
					return new GetLoanResponse(false, "Bank " + this.bank.getId() + " loan request failed for user " + account.emailAddress + ". ExecutionException", "", 0);
				}
			}
			this.sequenceNbr++;
			
			// Check if all operations were successful
			if ((loanAmount + extLoanSum) > account.getCreditLimit()) {
				logger.info(this.bank.getTextId() + ": Loan refused at bank " + this.bank.getId() + ". Total credit limit exceeded");
				return new GetLoanResponse(false, "Loan refused at bank " + this.bank.getId() + ". Total credit limit exceeded", "", 0);
			}
			else {
				newLoanId = this.bank.createLoan(account.emailAddress, accountNbr, loanAmount);
				logger.info(this.bank.getTextId() + ": Loan approved for user " + account.emailAddress + ", amount " + loanAmount + " at bank " + this.bank.getId());
			}
			
			return new GetLoanResponse(true, "Loan approved at bank " + this.bank.getId() + ".", "", newLoanId);
		}
	}

	@Override
	public OpenAccountResponse openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws RemoteException {

		logger.info("-------------------------------");
		logger.info(this.bank.getTextId() + ": Client invoked openAccount(emailAddress:" + emailAddress + ")");

		OpenAccountResponse resp = this.bank.createAccount(firstName, lastName, emailAddress, phoneNumber, password);
	
		if (resp.result) {
			logger.info(this.bank.getTextId() + " successfully opened an account for user " + emailAddress + " with account number " + resp.accountNbr);
		}
		else {
			logger.info(this.bank.getTextId() + " failed to open an account for user " + emailAddress);
		}
		return resp;
	}

	//
	// Operation performed by Managers
	//
	
	@Override
	public ServerResponse delayPayment(int loanId, Date currentDueDate, Date newDueDate) throws RemoteException {

		synchronized (lockObject) {

			logger.info("-------------------------------");
			logger.info(this.bank.getTextId() + ": Client invoked delayPayment(loanId:" + loanId + ")");
			
			Loan loan = this.bank.getLoanById(loanId);
			if (loan == null) {
				logger.info(this.bank.getTextId() + ": Loan id " + loanId + " does not exist");
				return new ServerResponse(false, "", "Loan id " + loanId + " does not exist");
			}
//			if (!loan.dueDate.equals(currentDueDate)) {
//				logger.info(this.bank.getTextId() + ": Loan id " + loanId + " does not exist");
//				return new ServerResponse(false, "", "Loan id " + loanId + " - currentDate argument mismatch");
//			}
			if (!loan.dueDate.before(newDueDate)) {
				logger.info(this.bank.getTextId() + ": Loan id " + loanId + " - currentDueDate argument must be later than the actual current due date of the loan");
				return new ServerResponse(false, "", " Loan id " + loanId + " - currentDueDate argument must be later than the actual current due date of the loan");
			}
			
			loan.setDueDate(newDueDate);
		}

		logger.info(this.bank.getTextId() + " loan " + loanId + " successfully delayed");
		return new ServerResponse(true, this.bank.getTextId() + " loan " + loanId + " successfully delayed", "");
	}

	@Override
	public String printCustomerInfo() throws RemoteException {

		logger.info("-------------------------------");
		logger.info(this.bank.getTextId() + ": Client invoked printCustomerInfo()");

		String result = new String();
		result = "------ ACCOUNTS ------\n";
		for (String key : this.bank.accounts.keySet()) {
			ThreadSafeHashMap<Integer, Account> accountsByLetter = this.bank.accounts.get(key);
			for (Integer accountId : accountsByLetter.keySet()) {
				Account account = accountsByLetter.get(accountId);
				result += account.toString() + "\n";
			}
		}
		
		result += "------ LOANS ------\n";
		for (String key : this.bank.loans.keySet()) {
			ThreadSafeHashMap<Integer, Loan> loansByLetter = this.bank.loans.get(key);
			for (Integer loanId : loansByLetter.keySet()) {
				Loan loan = loansByLetter.get(loanId);
				result += loan.toString() + "\n";
			}
		}
		//System.out.println(result);
		return result;
	}
}
