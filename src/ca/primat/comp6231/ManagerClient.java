package ca.primat.comp6231;

/**
 * The manager client application
 * 
 * @author mat
 * 
 */
public class ManagerClient extends Client<BankServerManagerInterface> {

	/**
	 * Constructor
	 */
	public ManagerClient() {
		super();
	}
	
	/**
	 * Entry point of the manager application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new ManagerClient();
	}

}
