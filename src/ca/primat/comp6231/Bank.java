package ca.primat.comp6231;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ca.primat.comp6231.response.OpenAccountResponse;

public class Bank {

	final protected String id;
	final protected InetSocketAddress udpAddress;
	public HashMap<String, ThreadSafeHashMap<Integer, Account>> accounts;
	public HashMap<String, ThreadSafeHashMap<Integer, Loan>> loans;

	protected static int nextAccountNbr = 100;
	protected static int nextLoanId = 1000;
	
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param udpAddress
	 */
	public Bank(String id, InetSocketAddress udpAddress) {
		
		super();
		this.id = id;
		this.udpAddress = udpAddress;
		this.accounts = new HashMap<String, ThreadSafeHashMap<Integer, Account>>();
		this.loans = new HashMap<String, ThreadSafeHashMap<Integer, Loan>>();
		char ch;
		
		// Pre-fill the first dimension of the accounts and loans arrays otherwise we need to synchronize access in order
		// to test existence of an entry
		for (ch = 'A'; ch <= 'Z'; ++ch) {
			accounts.put(String.valueOf(ch), new ThreadSafeHashMap<Integer, Account>()); 
		}
		for (ch = 'A'; ch <= 'Z'; ++ch) {
			loans.put(String.valueOf(ch), new ThreadSafeHashMap<Integer, Loan>()); 
		}
	}
	
	/**
	 * Method used for clearing test data
	 */
	public void resetBankData() {
		char ch;
		for (ch = 'A'; ch <= 'Z'; ++ch) {
			accounts.put(String.valueOf(ch), new ThreadSafeHashMap<Integer, Account>()); 
		}
		for (ch = 'A'; ch <= 'Z'; ++ch) {
			loans.put(String.valueOf(ch), new ThreadSafeHashMap<Integer, Loan>()); 
		}
	}
	
	/**
	 * Loop through the data structure to find an account which corresponds to a provided account number
	 * 
	 * @param accountNbr
	 * @return
	 */
	public Boolean accountExists(int accountNbr) {
		
		for (String key : this.accounts.keySet()) {
			ThreadSafeHashMap<Integer, Account> accountsByLetter = this.accounts.get(key);
			for (Integer accountId : accountsByLetter.keySet()) {
				Account account = accountsByLetter.get(accountId);
				if (account.accountNbr == accountNbr) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Gets an account by its account number. If the password argument is non-empty, it validates it as well as the account number
	 * 
	 * @param accountNbr
	 * @param password
	 * @return
	 */
	public Account authenticateAccount(int accountNbr, String password) {
		
		for (String firstLetter : this.accounts.keySet()) {
			ThreadSafeHashMap<Integer, Account> accountsByLetter = this.accounts.get(firstLetter);
			Account account = accountsByLetter.get(accountNbr);
			if (account == null || !account.password.equals(password)) {
				return null;
			}
			return account;
		}
		
		return null;
	}

	/**
	 * Create an account
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param password
	 * @return
	 */
	public OpenAccountResponse createAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
		
		String firstLetter = emailAddress.substring(0, 1).toUpperCase();
		ThreadSafeHashMap<Integer, Account> accounts = this.accounts.get(firstLetter);
		int newAccountNbr = 0;
		
		// TODO: Perform some field validation

		// Synchronize on the accounts list to get sightly better performance
		synchronized (accounts) {

			// Check if there is already an account with that email address
			for (Integer accNbr : accounts.keySet()) {
				Account account = accounts.get(accNbr);
				if (account.emailAddress.equals(emailAddress)) {
					return new OpenAccountResponse(false, "", "The account " + emailAddress + " already exists at bank" + this.getId(), account.accountNbr);
				}
			}
			
			newAccountNbr = nextAccountNbr++;
			Account newAccount = new Account(newAccountNbr, firstName, lastName, emailAddress, phoneNumber, password);
			
			// Create the account
			accounts.put(newAccountNbr, newAccount);
		}
		
		return new OpenAccountResponse(true, "", "Account " + emailAddress + "successfully create " + this.getId(), newAccountNbr);
	}

	/**
	 * Create a new loan
	 * 
	 * @param emailAddress
	 * @param accountNbr
	 * @param loanAmount
	 * @return
	 */
	public int createLoan(String emailAddress, int accountNbr, int loanAmount) {
		
		String firstLetter = emailAddress.substring(0, 1).toUpperCase();
		ThreadSafeHashMap<Integer, Loan> loans = this.loans.get(firstLetter);

		Date now = new Date();
		Calendar cal = Calendar.getInstance();
	    cal.setTime(now);
	    cal.add(Calendar.MONTH, 2);
		
	    int loanId = nextLoanId++;
		Loan loan = new Loan(accountNbr, emailAddress, loanAmount, cal.getTime(), loanId);
		loans.put(loanId, loan);
		return nextLoanId-1;
	}

	/**
	 * Gets the list of accounts with the same first character of their user name
	 * 
	 * @param emailAddress
	 * @return
	 */
	public ThreadSafeHashMap<Integer, Account> getAccountsByLetter(String emailAddress) {
		
		String firstLetter = emailAddress.substring(0, 1);
		return this.accounts.get(firstLetter);
	}

	/**
	 * Gets the account corresponding to the provided account number or null if no such account exists
	 * 
	 * @param emailAddress
	 * @return
	 */
	public Account getAccount(int accountNbr) {
		
		for (String firstLetter : this.accounts.keySet()) {
			ThreadSafeHashMap<Integer, Account> accountsByLetter = this.accounts.get(firstLetter);
			Account account = accountsByLetter.get(accountNbr);
			if (account != null) {
				return account;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Loan getLoanById(int id) {
		
		for (String firstLetter : this.loans.keySet()) {
			ThreadSafeHashMap<Integer, Loan> loansByLetter = this.loans.get(firstLetter);
			for (Integer loanId : loansByLetter.keySet()) {
				Loan loan = loansByLetter.get(loanId);
				if (loan.id == id) {
					return loan;
				}
			}
		}

		return null;
	}
	
	/**
	 * Gets the sum of all loans for the given account number
	 * 
	 * @param emailAddress
	 * @return
	 */
	public int getLoanSum(int accountNbr) {
		
		int result = 0;
		Date date = Calendar.getInstance().getTime();
		for (String key : this.loans.keySet()) {
			ThreadSafeHashMap<Integer, Loan> loansByLetter = this.loans.get(key);
			for (Integer loanId : loansByLetter.keySet()) {
				Loan loan = loansByLetter.get(loanId);
				if (loan.accountNbr == accountNbr && date.before(loan.dueDate)) {
					result += loan.amount;
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the sum of all loans for the given email address (aka username)
	 * 
	 * @param emailAddress
	 * @return
	 */
	public int getLoanSum(String emailAddress) {
		
		String firstLetter = emailAddress.substring(0, 1).toUpperCase();
		ThreadSafeHashMap<Integer, Loan> loansByLetter = this.loans.get(firstLetter);
		int result = 0;
		Date now = Calendar.getInstance().getTime();
		for (Integer loanId : loansByLetter.keySet()) {
			Loan loan = loansByLetter.get(loanId);
			if (loan.emailAddress.equals(emailAddress) && now.before(loan.dueDate)) {
				result += loan.amount;
			}
		}
		return result;
	}
	
	//
	// Getters and setters
	//

	/**
	 * Get a lower case version of the bank name
	 * 
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 
	 * @return
	 */
	public String getTextId() {
		return "Bank-" + this.getId();
	}

}
