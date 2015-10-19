package ca.primat.comp6231.response;

import java.io.Serializable;

public class GetLoanResponse extends ServerResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int newLoanId;
	
	/**
	 * Default Constructor
	 */
	public GetLoanResponse() {
		super();
		this.newLoanId = 0;
	}
	
	/**
	 * Constructor
	 * 
	 * @param result
	 * @param message
	 * @param debugMessage
	 * @param newAccountNbr
	 */
	public GetLoanResponse(Boolean result, String message, String debugMessage, int newLoanId) {
		super(result, message, debugMessage);
		this.newLoanId = newLoanId;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return "result: " + result + ", newLoanId: " + newLoanId + ", message: " + message + ", debugMessage: " + debugMessage;
	}
}
