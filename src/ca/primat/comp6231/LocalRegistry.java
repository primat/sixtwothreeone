package ca.primat.comp6231;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Wrapper over the RMI registry methods
 * 
 * @author mat
 *
 */
public class LocalRegistry {

	public static final int DEFAULT_PORT = 2020;

	protected int port;
	protected Registry reg;

	/**
	 * Constructor
	 * 
	 * @param port The port that the local registry will use
	 * @throws RemoteException
	 */
	public LocalRegistry(int port) throws RemoteException {
		super();	
		this.port = port;	
		this.reg = LocateRegistry.createRegistry(port);
	}

	/**
	 * Exports a bank server to accept incoming requests on the provided port. It then binds the server 
	 * to the RMI registry.
	 * 
	 * @param bankServer The bank server to export to the registry
	 * @return
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 */
	public Boolean exportAndBind(BankServer bankServer) throws RemoteException, AlreadyBoundException {
		
		Remote obj = UnicastRemoteObject.exportObject(bankServer, this.port);
		this.reg.bind(bankServer.bank.getId(), obj);
		return true;
	}
	
	//
	// Getters and setters
	//
	
	public int getPort() {
		return port;
	}
}
