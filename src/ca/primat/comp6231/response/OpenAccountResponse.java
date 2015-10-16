package ca.primat.comp6231.response;

public class OpenAccountResponse extends ServerResponse {

	final public int newAccountNbr;
	
	/**
	 * Constructor
	 * 
	 * @param result
	 * @param message
	 * @param debugMessage
	 * @param newAccountNbr
	 */
	public OpenAccountResponse(final Boolean result, final String message, final String debugMessage, final int newAccountNbr) {
		
		super(result, message, debugMessage);
		this.newAccountNbr = newAccountNbr;
	}
}
