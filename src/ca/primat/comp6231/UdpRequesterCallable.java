package ca.primat.comp6231;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Callable;

/**
 * Callable to run a UDP request to get loan info in a thread so that it doesn't block client operations
 * and be able to return a value to the parent thread
 * 
 * @author mat
 *
 */
public class UdpRequesterCallable implements Callable<LoanRequestStatus> {

	private volatile Bank sourceBank;
	private volatile Bank destinationBank;
	private volatile String emailAddress;
	private volatile int sequenceNbr;
	private DatagramSocket socket;
	
	/**
	 * Constructor
	 * 
	 * @param sourceBank
	 * @param destinationBank
	 * @param emailAddress
	 * @param sequenceNbr
	 */
	public UdpRequesterCallable(Bank sourceBank, Bank destinationBank, String emailAddress, int sequenceNbr) {
		
		this.sourceBank = sourceBank;
		this.destinationBank = destinationBank;
		this.emailAddress = emailAddress;
		this.sequenceNbr = sequenceNbr;
		
		try {
			this.socket = new DatagramSocket(destinationBank.udpAddress.getPort());
		} catch (SocketException e) {
			System.out.println("Bank " + sourceBank.getId() + "'s UDP port " + destinationBank.udpAddress.getPort() + " is already in use.");
			System.exit(1);
		}
	}

	/**
	 * @throws IOException 
	 * 
	 */
	public LoanRequestStatus call() throws IOException {

		final byte[] receiveData = new byte[1024];
		final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        LoanRequestStatus status = new LoanRequestStatus();
        String statusMessage = "Bank " + this.sourceBank.getId() + " loan request to " + this.destinationBank.getId() + " for user " + this.emailAddress + " could not be completed.";
        
        // Prepare the loan request message
		MessageRequestLoan message = new MessageRequestLoan();
		message.emailAddress = this.emailAddress;
		message.sequenceNbr = this.sequenceNbr;
		
		// Serialize the message
		oos = new ObjectOutputStream(baos);
		oos.writeObject(message);
		byte[] sendData = baos.toByteArray();
		
		// Send the message
		System.out.println("Bank " + this.sourceBank.getId() + " requesting loan info from bank " + this.destinationBank.getId() + " for user " + this.emailAddress + " (" + sendData.length + " bytes)");
		socket.send(new DatagramPacket(sendData, sendData.length, this.destinationBank.udpAddress));

		
		// Loop to get a response of the previosuly sent message (and discard other out of sync packets)
		Boolean loopFlag = true;
		while (loopFlag) {
			
			try {
				socket.setSoTimeout(3000);
			} catch (SocketException e1) {
				status.message = statusMessage + " (SocketException)";
				status.status = LoanRequestStatus.STATUS_FATAL;
				return status;
			}
			
			ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
            ObjectInputStream ois;
            Object obj = null;
            MessageResponseLoan resp = null;
            
			try {
				ois = new ObjectInputStream(bais);
				obj = ois.readObject();
	            bais.close();
	            ois.close();
			} catch (ClassNotFoundException e) {
				status.message = status.message = statusMessage + " (ClassNotFoundException)";
				status.status = LoanRequestStatus.STATUS_FATAL;
				e.printStackTrace();
				return status;
			} catch (IOException e) {
				status.message = status.message = statusMessage + " (IOException)";
				status.status = LoanRequestStatus.STATUS_FATAL;
				e.printStackTrace();
				return status;
			}
            
            
            if(obj instanceof MessageResponseLoan) {
                System.out.println("Loan Request");
                resp = (MessageResponseLoan) obj;
            } else {
                System.out.println("Not a login request");
                //return -1;
            }
            
            if (resp.sequenceNbr != this.sequenceNbr) {
            	System.out.println("Bank " + this.sourceBank.getId() + " received an out of sequence response from BankServer " + this.destinationBank.getId() + ". Discarding packet.");
                //return -1;
            }
            
            System.out.println(("Bank " + this.sourceBank.getId() + " received a successfull response from BankServer " + this.destinationBank.getId() + " (available amount: " + resp.amountAvailable + ")"));
				

		}
		
		
		
		
		socket.close();
		return status;

		
		//return 100;
	}
}
