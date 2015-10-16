package ca.primat.comp6231;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Bank {

	final protected String id;
	final protected InetSocketAddress udpAddress;
	public HashMap<String, ThreadSafeHashMap<String, Account>> accounts;
	public HashMap<String, ThreadSafeHashMap<String, Loan>> loans;

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
		this.accounts = new HashMap<String, ThreadSafeHashMap<String, Account>>();
		this.loans = new HashMap<String, ThreadSafeHashMap<String, Loan>>();
	}

	/**
	 * Check to see if an account already exists, by username/email address
	 * 
	 * @param emailAddress
	 * @return
	 */
	public Boolean accountExists(String emailAddress) {
		
		String firstLetter = emailAddress.substring(0, 1);
		
		// There is no list of accounts corresponding to the provided username/email address
		if (!this.accounts.containsKey(firstLetter)) {
			return false;
		}
		
		// Get the list of accounts and check if an account with the same email address already exists
		ThreadSafeHashMap<String, Account> accountsByLetter = this.accounts.get(firstLetter);
		
		if (!accountsByLetter.containsKey(emailAddress)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Loop through the data structure to find an account which corresponds to a provided account number
	 * 
	 * @param accountNbr
	 * @return
	 */
	public Boolean accountExists(int accountNbr) {
		
		for (String key : this.accounts.keySet()) {
			ThreadSafeHashMap<String, Account> accountsByLetter = this.accounts.get(key);
			for (String accountId : accountsByLetter.keySet()) {
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
			ThreadSafeHashMap<String, Account> accountsByLetter = this.accounts.get(firstLetter);
			for (String username : accountsByLetter.keySet()) {
				Account account = accountsByLetter.get(username);
				if (password.length() > 0 && account.password == password) {
					return account;
				}
			}
		}
		
		return null;
	}
	
	
//	public Account addLoan(int accountNumber, int amount, Date dueDate) {
//		Loan loan = new Loan(amount, amount, dueDate, nextLoanId++);
//		
//		return null;
//	}

	/**
	 * Gets the list of accounts with the same first character of their user name
	 * 
	 * @param emailAddress
	 * @return
	 */
	public ThreadSafeHashMap<String, Account> getAccountsByLetter(String emailAddress) {
		
		String firstLetter = emailAddress.substring(0, 1);
		return this.accounts.get(firstLetter);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Loan getLoanById(int id) {
		
		for (String firstLetter : this.loans.keySet()) {
			ThreadSafeHashMap<String, Loan> loansByLetter = this.loans.get(firstLetter);
			for (String loanId : loansByLetter.keySet()) {
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
			ThreadSafeHashMap<String, Loan> loansByLetter = this.loans.get(key);
			for (String loanId : loansByLetter.keySet()) {
				Loan loan = loansByLetter.get(loanId);
				if (loan.accountNbr == accountNbr && date.before(loan.dueDate)) {
					result += loan.amount;
				}
			}
		}
		return result;
	}
	
	
	
	
	/**
	 * Gets an account by its account number. If the password argument is non-empty, it validates it as well as the account number
	 * 
	 * @param accountNbr
	 * @param password
	 * @return
	 */
//	public Account getUsernameByAccontNbr(int accountNbr) {
//		
//		for (String firstLetter : this.accounts.keySet()) {
//			ThreadSafeHashMap<String, Account> accountsByLetter = this.accounts.get(firstLetter);
//			for (String username : accountsByLetter.keySet()) {
//				Account account = accountsByLetter.get(username);
//				if (password.length() > 0 && account.password == password) {
//					return account;
//				}
//			}
//		}
//		return null;
//	}
	
	
	
	//
	// Getters and setters
	//

	/**
	 * Get a lower case version of the bank name
	 * 
	 * @return
	 */
	public String getId() {
		return this.getId().toLowerCase();
	}
	
	public Account getAccount(String username) {
		
		return null;
	}
	
}