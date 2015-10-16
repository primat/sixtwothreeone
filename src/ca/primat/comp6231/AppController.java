package ca.primat.comp6231;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
	private CustomerClient cust4;
	private ManagerClient man1;
	private ManagerClient man2;
	private ManagerClient man3;
	private ManagerClient man4;
	private LocalRegistry registry;
	
	/**
	 * The application launcher
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		new AppController();
	}

	/**
	 * Use the AppController constructor to choose a scenario to run
	 */
	public AppController() {
		super();

		this.createRmiRegistry();
		this.createBanks();
		this.initBankData();
		
		// Create a few customer clients in their own threads
//		final Thread t1 = new Thread() {
//			@Override
//			public void run() {
//				System.out.println("Starting customer client #1");
//				cust1 = new CustomerClient();
//				cust1.com("rbc", true);
//			}
//		};
//		final Thread t2 = new Thread() {
//			@Override
//			public void run() {
//				System.out.println("Starting customer client #2");
//				cust2 = new CustomerClient();
//				cust2.com("rbc", false);
//			}
//		};
//		final Thread t3 = new Thread() {
//			@Override
//			public void run() {
//				System.out.println("Starting customer client #3");
//				cust3 = new CustomerClient();
//				cust3.com("cibc", true);
//			}
//		};
//
//		t1.start();
//		t2.start();
//		t3.start();
//		cust4.com("rbc", false);
//
//		Runnable task3 = () -> {
//			System.out.println("Starting customer client #3");
//			cust3 = new CustomerClient();
//			cust3.com("cibc", true);
//		};
//		task3.run();
//		Thread t3 = new Thread(task3);
//		t3.start();
//
//
//		System.out.println("Starting customer client #4");
//		cust4 = new CustomerClient();
//
//
//		try {
//			bankServer1.printCustomerInfo();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//		System.out.println("Starting customer client #1");
//		CustomerClient cust1 = new CustomerClient();
//		System.out.println("Starting manager #1");
//		ManagerClient man1 = new ManagerClient();
//		man1.printCustomerInfo("rbc");
//		man1.printCustomerInfo("cibc");
//		man1.printCustomerInfo("bmo");
//
//		cust1.openAccount("rbc", "Kyle", "Reese", "kylereese@gmail.com", "15145145143", "kylereese");
//		man1.printCustomerInfo("rbc");
	}
	
	/**
	 * Initialize the bank data
	 */
	private void initBankData() {
		
		// Prepopulate records in the banks
		this.bankServer1.openAccount("John", "Doe", "rbccustomer1@gmail.com", "15145145145", "jondoe");
		this.bankServer1.openAccount("Sarah", "Conor", "rbccustomer2@gmail.com", "15145145144", "sarahconor");
		this.bankServer1.openAccount("Kyle", "Reese", "kylereese@gmail.com", "15145145143", "kylereese");
		
		this.bankServer2.openAccount("Vincent", "Vega", "cibccustomer1@gmail.com", "15145145155", "vincentvega");
		this.bankServer2.openAccount("Jules", "Winnfield", "cibccustomer2@gmail.com", "15145145144", "juleswinnfield");
		this.bankServer2.openAccount("Mia", "Wallace", "miawallace@gmail.com", "15145145163", "miawallace");

		this.bankServer3.openAccount("Charles", "Xavier", "bmocustomer1@gmail.com", "15145145145", "charlesxavier");
		this.bankServer3.openAccount("Lois", "Lane", "bmocustomer2@gmail.com", "15145145244", "loislane");
		this.bankServer3.openAccount("Elanor", "Gamgee", "elanorgamgee@gmail.com", "15145145343", "elanorgamgee");
		
		//this.bankServer3.bank.getLoan(accountNumber, amount, dueDate);
		
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
		this.bankServer1 = new BankServer(this.registry, bankCollection, bank1.id);
		System.out.println("Server " + bankServer1.bank.getId() + " started and bound to the registry");
		this.bankServer2 = new BankServer(this.registry, bankCollection, bank2.id);
		System.out.println("Server " + bankServer2.bank.getId() + " started and bound to the registry");
		this.bankServer3 = new BankServer(this.registry, bankCollection, bank3.id);
		System.out.println("Server " + bankServer3.bank.getId() + " started and bound to the registry");
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

}
