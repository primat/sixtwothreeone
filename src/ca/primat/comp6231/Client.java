package ca.primat.comp6231;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class Client<T extends Remote> {

	protected HashMap<String, T> servers;
	/**
	 * 
	 * Constructor
	 */
	public Client() {
		super();
		this.servers = new HashMap<String, T>();
	}
	
	/**
	 * Get the default URL of the registry
	 * 
	 * @return The URL to the registry
	 */
	protected String getDefaultRegistryUrl() {
		return "rmi://localhost:" + LocalRegistry.DEFAULT_PORT + "/";
	}

	/**
	 * Gets the BankServer object
	 * 
	 * @param serverId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T getBankServer(String serverId) {
		
		if (this.servers.containsKey(serverId)) {
			return this.servers.get(serverId);
		}

		T server = null;
		String url = this.getDefaultRegistryUrl() + serverId;

		try {
			System.out.println("Calling registry at : " + url);
			System.setSecurityManager(new RMISecurityManager());
			server = (T) Naming.lookup(url);
			if (server != null) {
				this.servers.put(serverId, server);
			}
			System.out.println("Client connected to server!");
		} catch (RemoteException e1) {
			// e1.printStackTrace();
			System.out.println("Fatal error: RemoteException when connecting to " + url);
		} catch (MalformedURLException e1) {
			// e1.printStackTrace();
			System.out.println("Fatal error: Malformed URL in " + url);
		} catch (NotBoundException e1) {
			// e1.printStackTrace();
			System.out.println("Fatal error: Unable to bind to " + url);
		}

		return server;
	}
	
}
