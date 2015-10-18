package ca.primat.comp6231.response;

public class OpenAccountResponse extends ServerResponse {

	final public int accountNbr;
	
	/**
	 * Constructor
	 * 
	 * @param result
	 * @param message
	 * @param debugMessage
	 * @param accountNbr
	 */
	public OpenAccountResponse(final Boolean result, final String message, final String debugMessage, final int accountNbr) {
		
		super(result, message, debugMessage);
		this.accountNbr = accountNbr;
	}
}
