package ca.primat.comp6231;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Alexandre Hudon
 * @date 18/09/2013
 * RMI-Tutorial, COMP 6231 - Text Scrambler Interface
 * This class needs to be modified by the students in order to define it as a Java RMI Interface.
 */
public interface TextScramblerInterface extends Remote {

	public String testInputText(String inputText) throws RemoteException;
	public String reverse(String inputText) throws RemoteException;
	public String scramble(String inputText) throws RemoteException;
	
}
