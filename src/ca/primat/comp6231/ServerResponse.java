package ca.primat.comp6231;

public class ServerResponse {

	public Boolean result = false;
	public String message = "";
	
	/**
	 * Constructor
	 * 
	 * @param result Whether the operations was successful or not
	 * @param message A (UI) message to describe the error
	 */
	public ServerResponse(Boolean result, String message) {
		super();
		this.result = result;
		this.message = message;
	}
	
}
