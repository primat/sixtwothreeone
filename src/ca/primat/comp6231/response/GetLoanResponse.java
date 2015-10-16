package ca.primat.comp6231.response;

public class GetLoanResponse extends ServerResponse {

	final public int newLoanId;
	
	/**
	 * Constructor
	 * 
	 * @param result
	 * @param message
	 * @param debugMessage
	 * @param newAccountNbr
	 */
	public GetLoanResponse(final Boolean result, final String message, final String debugMessage, final int newLoanId) {
		
		super(result, message, debugMessage);
		this.newLoanId = newLoanId;
	}
}
