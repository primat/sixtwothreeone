package ca.primat.comp6231.response;

import java.io.Serializable;

import ca.primat.comp6231.BankServerResponseInterface;

public class ServerResponse implements BankServerResponseInterface, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Boolean result;
	public String message;
	public String debugMessage;
	
	/**
	 * Default Constructor
	 */
	public ServerResponse() {
		super();
		this.result = false;
		this.message = "";
		this.debugMessage = "";
	}

	/**
	 * Constructor
	 * 
	 * @param result Whether the operations was successful or not
	 * @param message A (UI) message to describe the error
	 */
	public ServerResponse(Boolean result, String message, String debugMessage) {
		
		//super();
		this.result = result;
		this.message = message;
		this.debugMessage = debugMessage;
	}
}
