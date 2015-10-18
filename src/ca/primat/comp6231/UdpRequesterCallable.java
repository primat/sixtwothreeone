package ca.primat.comp6231;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

import java.util.logging.Logger;

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
	private Logger logger;
	
	/**
	 * Constructor
	 * 
	 * @param sourceBank
	 * @param destinationBank
	 * @param emailAddress
	 * @param sequenceNbr
	 * @param logger
	 */
	public UdpRequesterCallable(Bank sourceBank, Bank destinationBank, String emailAddress, int sequenceNbr, Logger logger) {
		
		this.sourceBank = sourceBank;
		this.destinationBank = destinationBank;
		this.emailAddress = emailAddress;
		this.sequenceNbr = sequenceNbr;
		this.logger = logger;
		
		try {
			this.socket = new DatagramSocket(destinationBank.udpAddress.getPort()+10);
		} catch (SocketException e) {
			System.out.println("Bank " + sourceBank.getId() + "'s UDP port " + destinationBank.udpAddress.getPort() + " is already in use.");
			System.exit(1);
		}
	}

	/**
	 * Makes a UDP request to another bank get loan information on a particular user
	 */
	public LoanRequestStatus call() {

		try {
			
			//
			// REQUESTING
			//
			
			// Init data structures
			final DatagramSocket clientSocket = new DatagramSocket();
			final byte[] receiveData = new byte[1024];
			final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos;
	        
	        // Prepare the loan request message
			MessageRequestLoan message = new MessageRequestLoan();
			message.emailAddress = this.emailAddress;
			
			// Serialize the message
			oos = new ObjectOutputStream(baos);
			oos.writeObject(message);
			byte[] sendData = baos.toByteArray();
			
			logger.info(this.sourceBank.getTextId() + " requesting loan info for user " + this.emailAddress + " at bank " + this.destinationBank.getTextId());
			
			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationBank.udpAddress);
			clientSocket.send(sendPacket);
	
			//
			// GETTING RESPONSE
			//
			clientSocket.setSoTimeout(10000);
			
			try {

				clientSocket.receive(receivePacket);
				
				// Parse the response data
				byte[] recvData = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), recvData, 0, receivePacket.getLength());

				ByteArrayInputStream bais = new ByteArrayInputStream(recvData);
	            ObjectInputStream ois;
	            Object obj = null;
	            MessageResponseLoan resp = null;
	            
				try {
					ois = new ObjectInputStream(bais);
					obj = ois.readObject();
		            bais.close();
		            ois.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

	            if(obj instanceof MessageResponseLoan) {
	                resp = (MessageResponseLoan) obj;
	            } else {
	                System.out.println("MessageResponseLoan!");
	                System.exit(1);
	            }
				
				logger.info(this.sourceBank.getTextId() + " received loan info response from  " + this.destinationBank.getTextId() + " for user " + this.emailAddress + ": " + resp.amountAvailable);

			} catch (final SocketTimeoutException ste) {
				System.out.println("Timeout Occurred: Packet assumed lost");
			}
			clientSocket.close();

		} catch (final SocketException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
//	/**
//	 * Makes a UDP request to another bank get loan information on a particular user (text version)
//	 */
//	public LoanRequestStatus call() {
//		
//		final String data = this.emailAddress;
//		//final String statusMessage = "Bank " + this.sourceBank.getId() + " loan request to " + this.destinationBank.getId() + " for user " + this.emailAddress + " could not be completed.";
//		
//		try {
//			final DatagramSocket clientSocket = new DatagramSocket();
//			final byte[] receiveData = new byte[1024];
//			final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//			byte[] sendData = new byte[data.length()];
//			sendData = data.getBytes();
//			
//			logger.info(this.sourceBank.getTextId() + " requesting loan info for user " + this.emailAddress + " at bank " + this.destinationBank.getTextId());
//			
//			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationBank.udpAddress);
//			clientSocket.send(sendPacket);
//			clientSocket.setSoTimeout(10000);
//			
//			try {
//				
//				clientSocket.receive(receivePacket);
//				
//				// Parse the response data
//				byte[] recvData = new byte[receivePacket.getLength()];
//		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), recvData, 0, receivePacket.getLength());
//				final String modifiedSentence = new String(recvData);
//				
//				logger.info(this.sourceBank.getTextId() + " received loan info response from  " + this.destinationBank.getTextId() + " for user " + this.emailAddress + ": " + modifiedSentence);
//
//			} catch (final SocketTimeoutException ste) {
//				System.out.println("Timeout Occurred: Packet assumed lost");
//			}
//			clientSocket.close();
//
//		} catch (final SocketException e) {
//			e.printStackTrace();
//		} catch (final IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
	
//	/**
//	 * UDP request - version with object marshalling
//	 * 
//	 * @throws IOException 
//	 * 
//	 */
//	public LoanRequestStatus calls() throws IOException {
//
//		final byte[] receiveData = new byte[1024];
//		final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos;
//        LoanRequestStatus status = new LoanRequestStatus();
//        String statusMessage = "Bank " + this.sourceBank.getId() + " loan request to " + this.destinationBank.getId() + " for user " + this.emailAddress + " could not be completed.";
//        
//        // Prepare the loan request message
//		MessageRequestLoan message = new MessageRequestLoan();
//		message.emailAddress = this.emailAddress;
//		message.sequenceNbr = this.sequenceNbr;
//		
//		// Serialize the message
//		oos = new ObjectOutputStream(baos);
//		oos.writeObject(message);
//		byte[] sendData = baos.toByteArray();
//		
//		// Send the message
//		System.out.println("Bank " + this.sourceBank.getId() + " requesting loan info from bank " + this.destinationBank.getId() + " for user " + this.emailAddress + " (" + sendData.length + " bytes)");
//		socket.send(new DatagramPacket(sendData, sendData.length, this.destinationBank.udpAddress));
//
//		// Loop to get a response of the previously sent message (and discard other out of sync packets)
//		Boolean loopFlag = true;
//		while (loopFlag) {
//			
//			try {
//				socket.setSoTimeout(3000);
//			} catch (SocketException e1) {
//				status.message = statusMessage + " (SocketException)";
//				status.status = LoanRequestStatus.STATUS_FATAL;
//				return status;
//			}
//
//			ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
//            ObjectInputStream ois;
//            Object obj = null;
//            MessageResponseLoan resp = null;
//            
//			try {
//				ois = new ObjectInputStream(bais);
//				obj = ois.readObject();
//	            bais.close();
//	            ois.close();
//			} catch (ClassNotFoundException e) {
//				status.message = status.message = statusMessage + " (ClassNotFoundException)";
//				status.status = LoanRequestStatus.STATUS_FATAL;
//				e.printStackTrace();
//				return status;
//			} catch (IOException e) {
//				status.message = status.message = statusMessage + " (IOException)";
//				status.status = LoanRequestStatus.STATUS_FATAL;
//				e.printStackTrace();
//				return status;
//			}
//            
//            
//            if(obj instanceof MessageResponseLoan) {
//                System.out.println("Loan Request");
//                resp = (MessageResponseLoan) obj;
//            } else {
//                System.out.println("Not a login request");
//                //return -1;
//            }
//            
//            if (resp.sequenceNbr != this.sequenceNbr) {
//            	System.out.println("Bank " + this.sourceBank.getId() + " received an out of sequence response from BankServer " + this.destinationBank.getId() + ". Discarding packet.");
//                //return -1;
//            }
//            
//            System.out.println(("Bank " + this.sourceBank.getId() + " received a successfull response from BankServer " + this.destinationBank.getId() + " (available amount: " + resp.amountAvailable + ")"));
//		}
//		
//		socket.close();
//		return status;
//	}
}
