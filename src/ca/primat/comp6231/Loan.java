package ca.primat.comp6231;

import java.util.Date;

/**
 * The loan data object
 * 
 * @author mat
 *
 */
public class Loan {
	
	protected int accountNbr; // Foreign key
	protected int amount;
	protected Date dueDate;
	protected int id; // Primary key
	
	/**
	 * Constructor
	 * 
	 * @param accountNbr the account number of the load owner
	 * @param amount The amount of the load in dollars
	 * @param dueDate The due date of the loan
	 * @param id the id of this loan
	 */
	public Loan(int accountNbr, int amount, Date dueDate, int id) {
		super();
		this.accountNbr = accountNbr;
		this.amount = amount;
		this.dueDate = dueDate;
		this.id = id;
	}

	public int getAccountNbr() {
		return accountNbr;
	}

	public int getAmount() {
		return amount;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public int getId() {
		return id;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

}
