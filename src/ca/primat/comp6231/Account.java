package ca.primat.comp6231;

/**
 * Account "entity" stores account data
 * 
 * @author mat
 *
 */
public class Account {
	
	protected int accountNbr;
	protected String firstName;
	protected String lastName;
	protected String emailAddress;
	protected int phoneNbr;
	protected String password;
	protected double creditLimit;
	
	public Account(int accountNbr, String firstName, String lastName, String emailAddress, int phoneNbr, 
			String password, double creditLimit) {
		super();
		this.accountNbr = accountNbr;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.phoneNbr = phoneNbr;
		this.password = password;
		this.creditLimit = creditLimit;
	}

	public int getAccountNbr() {
		return accountNbr;
	}

	public void setAccountNbr(int accountNbr) {
		this.accountNbr = accountNbr;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public int getPhoneNbr() {
		return phoneNbr;
	}

	public void setPhoneNbr(int phoneNbr) {
		this.phoneNbr = phoneNbr;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}
	
}
