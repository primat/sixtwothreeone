package ca.primat.comp6231;

import java.net.InetSocketAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

import ca.primat.comp6231.response.GetLoanResponse;
import ca.primat.comp6231.response.OpenAccountResponse;

/**
 * Main launcher class for the comp6231 assignment #1
 * 
 * @author mat
 *
 */
public class AppController {

	private BankServer bankServer1;
	private BankServer bankServer2;
	private BankServer bankServer3;
	private CustomerClient cust1;
	private CustomerClient cust2;
	private CustomerClient cust3;
//	private CustomerClient cust4;
	private ManagerClient man1;
//	private ManagerClient man2;
//	private ManagerClient man3;
//	private ManagerClient man4;
	private LocalRegistry registry;
	private Object commonLock = new Object();
	
	/**
	 * The application launcher
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String args[]) throws InterruptedException {
		new AppController();
	}

	/**
	 * Use the AppController constructor to choose a scenario to run
	 * @throws InterruptedException 
	 */
	public AppController() throws InterruptedException {
		
		super();
		this.createRmiRegistry();
		this.createBanks();
		//this.initBankData();
		this.bindAndExportBankServers();

		// Run individual tests
		//this.test1();
		this.test2();
		//this.test3();
		//this.test4();
		//...
		
	}
	
	/**
	 * Test method #1
	 * 
	 * @throws InterruptedException
	 */
	protected void test1() throws InterruptedException {

		// Create a few customer clients in their own threads and make them do some operations
		final Thread tc1 = new Thread() {
			@Override
			public void run() {
				cust1 = new CustomerClient();
				int accNbr1 = cust1.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "15145145145", "jondoe");
				cust1.openAccount("bmo", "Charles", "Xavier", "charlesxavier@gmail.com", "15145145145", "charlesxavier");
				cust1.openAccount("cibc", "Vincent", "Vega", "vincentvega@gmail.com", "15145145155", "vincentvega");
				
				// Add a loan for user jondoe@gmail.com at bank "rbc"
				cust1.getLoan("rbc", accNbr1, "jondoe", 500);
			}
		};
		final Thread tc2 = new Thread() {
			@Override
			public void run() {
				cust2 = new CustomerClient();
				cust2.openAccount("rbc", "Sarah", "Conor", "sarahconor@gmail.com", "15145145144", "sarahconor");
				cust2.openAccount("bmo", "Lois", "Lane", "loislane@gmail.com", "15145145244", "loislane");
				cust2.openAccount("cibc", "Jules", "Winnfield", "juleswinnfield@gmail.com", "15145145144", "juleswinnfield");
			}
		};
		final Thread tc3 = new Thread() {
			@Override
			public void run() {
				cust3 = new CustomerClient();
				cust3.openAccount("rbc", "Kyle", "Reese", "kylereese@gmail.com", "15145145163", "kylereese");
				cust3.openAccount("bmo", "Elanor", "Gamgee", "elanorgamgee@gmail.com", "15145145343", "elanorgamgee");
				int accNbr1 = cust3.openAccount("cibc", "John", "Doe", "jondoe@gmail.com", "15145145145", "jondoe");
				
				// Add a loan for user jondoe@gmail.com at bank "rbc"
				cust1.getLoan("cibc", accNbr1, "jondoe", 600);
			}
		};

		tc1.start();
		tc2.start();
		tc3.start();

		final Thread tm1 = new Thread() {
			@Override
			public void run() {
				//System.out.println("Starting manager client #1");
				man1 = new ManagerClient();
				man1.printCustomerInfo("rbc");
				man1.printCustomerInfo("cibc");
				man1.printCustomerInfo("bmo");
			}
		};

		Thread.sleep(1000);
		tm1.start();

		// Wait for all threads to complete before proceeding and clear bank data
		tc1.join();
		tc2.join();
		tc3.join();
		tm1.join();
		this.clearBankData();
	}
	
	/**
	 * Test method #2
	 * 
	 * @throws InterruptedException
	 */
	protected void test2() throws InterruptedException {

		// Create a few customer clients in their own threads and make them do some operations
		final Thread tc1 = new Thread() {
			@Override
			public void run() {
				CustomerClient cust = new CustomerClient();
				int accNbr1 = cust.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "15145145145", "jondoe");
				// Add a loan for user jondoe@gmail.com at bank "rbc"
				cust.getLoan("rbc", accNbr1, "jondoe", 500);
			}
		};

		final Thread tc3 = new Thread() {
			@Override
			public void run() {
				CustomerClient cust = new CustomerClient();
				int accNbr2 = cust.openAccount("cibc", "John", "Doe", "jondoe@gmail.com", "15145145145", "jondoe");
				// Add a loan for user jondoe@gmail.com at bank "rbc"
				cust.getLoan("cibc", accNbr2, "jondoe", 600);
			}
		};

		tc1.start();
		tc3.start();

		final Thread tm1 = new Thread() {
			@Override
			public void run() {
				//System.out.println("Starting manager client #1");
				man1 = new ManagerClient();
				man1.printCustomerInfo("rbc");
				man1.printCustomerInfo("cibc");
			}
		};

		// Wait for all threads to complete before proceeding and clear bank data
		tc1.join();
		tc3.join();

		tm1.start();
		tm1.join();
		
		this.clearBankData();
	}
	
	/**
	 * Prepares the BankServers to be used for RMI
	 */
	private void bindAndExportBankServers() {

		try {
			this.registry.exportAndBind(this.bankServer1);
		} catch (RemoteException e) {
			System.out.println("Error: Remote exception while exporting and binding the bank server.");
			e.printStackTrace();
			System.exit(1);
		} catch (AlreadyBoundException e) {
			System.out.println("Error: Trying to bind server " + this.bankServer1.bank.getId() + " to the registry but it is already bound.");
		}

		try {
			this.registry.exportAndBind(this.bankServer2);
		} catch (RemoteException e) {
			System.out.println("Error: Remote exception while exporting and binding the bank server.");
			e.printStackTrace();
			System.exit(1);
		} catch (AlreadyBoundException e) {
			System.out.println("Error: Trying to bind server " + this.bankServer2.bank.getId() + " to the registry but it is already bound.");
		}

		try {
			this.registry.exportAndBind(this.bankServer3);
		} catch (RemoteException e) {
			System.out.println("Error: Remote exception while exporting and binding the bank server.");
			e.printStackTrace();
			System.exit(1);
		} catch (AlreadyBoundException e) {
			System.out.println("Error: Trying to bind server " + this.bankServer3.bank.getId() + " to the registry but it is already bound.");
		}
	}
	
	/**
	 * Initialize the bank data
	 */
	@SuppressWarnings("unused")
	private void initBankData() {
		
		OpenAccountResponse oaResp1_1 = null;
		
		// Prepopulate records in the banks
		try {
			oaResp1_1 = this.bankServer1.openAccount("John", "Doe", "rbccustomer1@gmail.com", "15145145145", "jondoe");
			this.bankServer1.openAccount("Sarah", "Conor", "rbccustomer2@gmail.com", "15145145144", "sarahconor");
			this.bankServer1.openAccount("Kyle", "Reese", "kylereese@gmail.com", "15145145143", "kylereese");
			
			this.bankServer2.openAccount("Vincent", "Vega", "cibccustomer1@gmail.com", "15145145155", "vincentvega");
			this.bankServer2.openAccount("Jules", "Winnfield", "cibccustomer2@gmail.com", "15145145144", "juleswinnfield");
			this.bankServer2.openAccount("Mia", "Wallace", "miawallace@gmail.com", "15145145163", "miawallace");
	
			this.bankServer3.openAccount("Charles", "Xavier", "bmocustomer1@gmail.com", "15145145145", "charlesxavier");
			this.bankServer3.openAccount("Lois", "Lane", "bmocustomer2@gmail.com", "15145145244", "loislane");
			this.bankServer3.openAccount("Elanor", "Gamgee", "elanorgamgee@gmail.com", "15145145343", "elanorgamgee");
		} catch (RemoteException e) {
			System.out.println("Failed to initialize bank accounts");
			e.printStackTrace();
		}


		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		GetLoanResponse resp = null;
		try {
			this.bankServer1.printCustomerInfo();
			resp = this.bankServer1.getLoan(oaResp1_1.accountNbr, "jondoe", 300);
			this.bankServer1.printCustomerInfo();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the banks and bank servers
	 */
	private void createBanks() {

		// Create the 3 bank objects which will be passed to the servers
		Bank bank1 = new Bank("rbc", new InetSocketAddress("localhost", 10101));
		Bank bank2 = new Bank("cibc", new InetSocketAddress("localhost", 10102));
		Bank bank3 = new Bank("bmo", new InetSocketAddress("localhost", 10103));

		HashMap<String, Bank> bankCollection = new HashMap<String, Bank>();
		bankCollection.put(bank1.id, bank1);
		bankCollection.put(bank2.id, bank2);
		bankCollection.put(bank3.id, bank3);
		
		// Create the three bank servers - one for each Bank
		this.bankServer1 = new BankServer(bankCollection, bank1.id, commonLock);
		System.out.println("BankServer " + bankServer1.bank.getId() + " started and bound to the registry");
		this.bankServer2 = new BankServer(bankCollection, bank2.id, commonLock);
		System.out.println("BankServer " + bankServer2.bank.getId() + " started and bound to the registry");
		this.bankServer3 = new BankServer(bankCollection, bank3.id, commonLock);
		System.out.println("BankServer " + bankServer3.bank.getId() + " started and bound to the registry");
	}

	/**
	 * Create the RMI registry
	 */
	private void createRmiRegistry() {
		try {
			this.registry = new LocalRegistry(LocalRegistry.DEFAULT_PORT);
			System.out.println("Created the local RMI registry");
		} catch (RemoteException e) {
			System.out.println("Error: Cannot create a local RMI registry");
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets all bank data to the empty state
	 */
	private void clearBankData() {
		this.bankServer1.bank.resetBankData();
		this.bankServer2.bank.resetBankData();
		this.bankServer3.bank.resetBankData();
	}
}
