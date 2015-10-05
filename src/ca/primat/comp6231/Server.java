package ca.primat.comp6231;

import java.rmi.registry.Registry;

public class Server {

	protected Registry reg;

	public Server(Registry reg) {
		super();
		this.reg = reg;
	}
	
}
