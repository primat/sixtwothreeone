package ca.primat.comp6231;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
/**
 * @author Alexandre Hudon
 * @date 18/09/2013
 * RMI-Tutorial, COMP 6231 - Text Scrambler Server
 * This class needs to be modified by the students in order to define it as a Java RMI Server.
 * Provides the implementation of the TextScramblerInterface.
 */
public class TextScramblerServer implements TextScramblerInterface 
{
	public static void main(String args[]) {
		try {
			(new TextScramblerServer()).expertServer();
			System.out.println("Server is up and running!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void expertServer() throws Exception {
		Remote obj = UnicastRemoteObject.exportObject(this, 2020);
		Registry r = LocateRegistry.createRegistry(2020);
		r.bind("test", obj);
	}
	
	@Override //Return input text as-is.
	public String testInputText(String inputText) {
		
		return "Your input text is: " + inputText;
	}

	@Override //Return the string reversed.
	public String reverse(String inputText) {
		String reversedInput = "";
		for(int i=0; i<inputText.length();i++)
		{
			reversedInput=reversedInput+inputText.charAt((inputText.length()-1)-i);
		}
		return "Result: "+reversedInput;
	}

	@Override //Return the string scrambled.
	public String scramble(String inputText) {
		String scrambledInput="";

		for(int i=0; i<inputText.length();i++)
		{
			if(i%2==0)
			{
				scrambledInput=scrambledInput+inputText.charAt(i);
			}
			else
			{
				scrambledInput=inputText.charAt(i)+scrambledInput;
			}
		}
		return "Result: "+scrambledInput;
	}
}
