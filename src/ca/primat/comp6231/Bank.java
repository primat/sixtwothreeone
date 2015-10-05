package ca.primat.comp6231;

import java.util.ArrayList;
import java.util.HashMap;

public class Bank {

	protected String name;
	public HashMap<String, HashMap<String, Account>> accounts;
	public HashMap<String, ArrayList<String>> loans;
	
	/**
	 * Constructor
	 * 
	 * @param id The acronym of the bank's name
	 */
	public Bank(String name) {
		super();
		this.name = name;
	}

	//
	// Getters and setters
	//
	
	public String getName() {
		return name;
	}
	
	public Account getAccount(String username) {
		
		return null;
	}
	
}