package ca.primat.comp6231;

import java.util.Date;

public class CustomerLoan {

	protected int loanId;
	protected int accountNbr;
	protected int loanAmmount;
	protected Date dueDate;
	
	public CustomerLoan(int loanId, int accountNbr, int loanAmmount, Date dueDate) {
		super();
		this.loanId = loanId;
		this.accountNbr = accountNbr;
		this.loanAmmount = loanAmmount;
		this.dueDate = dueDate;
	}

	public int getLoanId() {
		return loanId;
	}

	public void setLoanId(int loanId) {
		this.loanId = loanId;
	}

	public int getAccountNbr() {
		return accountNbr;
	}

	public void setAccountNbr(int accountNbr) {
		this.accountNbr = accountNbr;
	}

	public int getLoanAmmount() {
		return loanAmmount;
	}

	public void setLoanAmmount(int loanAmmount) {
		this.loanAmmount = loanAmmount;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	
}
