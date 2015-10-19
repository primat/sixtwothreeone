package ca.primat.comp6231.response;

import java.io.Serializable;

public class OpenAccountResponse extends ServerResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int accountNbr;
	
	/**
	 * Default Constructor
	 */
	public OpenAccountResponse() {
		super();
		this.accountNbr = 0;
	}
	
	/**
	 * Constructor
	 * 
	 * @param result
	 * @param message
	 * @param debugMessage
	 * @param accountNbr
	 */
	public OpenAccountResponse(Boolean result, String message, String debugMessage, int accountNbr) {
		super(result, message, debugMessage);
		this.accountNbr = accountNbr;
	}
}
