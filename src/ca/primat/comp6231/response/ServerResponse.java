package ca.primat.comp6231.response;

public class ServerResponse {

	final public Boolean result;
	final public String message;
	final public String debugMessage;
	
	/**
	 * Constructor
	 * 
	 * @param result Whether the operations was successful or not
	 * @param message A (UI) message to describe the error
	 */
	public ServerResponse(final Boolean result, final String message, final String debugMessage) {
		
		super();
		this.result = result;
		this.message = message;
		this.debugMessage = debugMessage;
	}
}
