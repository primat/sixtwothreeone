package ca.primat.comp6231;

import java.rmi.RemoteException;

/**
 * Main launcher class for the comp6231 assignment #1
 * 
 * @author mat
 *
 */
public class AppController {

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
		
		// Create a local registry to start with
		LocalRegistry registry = null;
		try {
			registry = new LocalRegistry(LocalRegistry.DEFAULT_PORT);
			System.out.println("Created the local RMI registry");
		} catch (RemoteException e) {
			System.out.println("Error: Cannot create a local RMI registry");
			e.printStackTrace();
		}
		
		// Create the 3 bank objects which will be passed to the servers
		Bank bank1 = new Bank("RBC");
		Bank bank2 = new Bank("CIBC");
		Bank bank3 = new Bank("BMO");
		
		// Create the three bank servers - one for each Bank
		BankServer bankServer1 = new BankServer(registry, bank1);
		System.out.println("Server " + bankServer1.getId() + " started and bound to the registry");
		BankServer bankServer2 = new BankServer(registry, bank2);
		System.out.println("Server " + bankServer2.getId() + " started and bound to the registry");
		BankServer bankServer3 = new BankServer(registry, bank3);
		System.out.println("Server " + bankServer3.getId() + " started and bound to the registry");
		
		// Pre-populate records in the banks
		bankServer1.openAccount("John", "Doe", "rbccustomer1@gmail.com", "15145145145", "customerrbc1");
		bankServer1.openAccount("Sarah", "Conor", "rbccustomer2@gmail.com", "15145145144", "customerrbc2");
		bankServer1.openAccount("Kyle", "Reese", "kylereese@gmail.com", "15145145143", "kylereese");
		
		bankServer2.openAccount("Vincent", "Vega", "cibccustomer1@gmail.com", "15145145155", "customercibc1");
		bankServer2.openAccount("Jules", "Winnfield", "cibccustomer2@gmail.com", "15145145144", "customercibc2");
		bankServer2.openAccount("Mia", "Wallace", "miawallace@gmail.com", "15145145163", "miawallace");

		bankServer3.openAccount("Charles", "Xavier", "bmocustomer1@gmail.com", "15145145145", "customerrbc1");
		bankServer3.openAccount("Lois", "Lane", "bmocustomer2@gmail.com", "15145145244", "customerrbc2");
		bankServer3.openAccount("Elanor", "Gamgee", "elanorgamgee@gmail.com", "15145145343", "elanorgamgee");
		

	}
}
