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
import java.util.logging.Logger;

public class BankUdpListener implements Runnable {

	protected volatile Bank bank;
	protected Logger logger;

	/**
	 * Constructor - Run the UDP Listener
	 * 
	 * @param bank
	 * @param logger
	 */
	BankUdpListener(Bank bank, Logger logger) {

		this.bank = bank;
		this.logger = logger;
	}
	
	
	/**
	 * Plain text version
	 */
	@Override
	public void run() {
		
		DatagramSocket serverSocket = null;
		
		try {

			serverSocket = new DatagramSocket(this.bank.udpAddress);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];

			while (true) {

				//
				// LISTENER
				//
				
				receiveData = new byte[1024];
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// Wait for the packet
				logger.info(this.bank.getTextId() + " waiting for loan info request packet on " + this.bank.udpAddress.toString());
				serverSocket.receive(receivePacket);
				
				// Received a request. Parse the packet.
				byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
				final InetAddress remoteIpAddress = receivePacket.getAddress();
				final int remotePort = receivePacket.getPort();

				// Extract the receiver's message into the appropriate object
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
	            ObjectInputStream ois = new ObjectInputStream(bais);
	            Object obj;
				try {
					obj = ois.readObject();
				} catch (ClassNotFoundException e) {
					logger.info(this.bank.getTextId() + " an invalid packet from: " + remoteIpAddress + ":" + remotePort + ". Discarding it.");
					continue;
				}
	            bais.close();
	            ois.close();
	            MessageRequestLoan req = null;
	            
				if (obj instanceof MessageRequestLoan) {
					req = (MessageRequestLoan) obj;
				} else {
					logger.info(this.bank.getTextId() + " received an unknown request packet from " + remoteIpAddress + ":" + remotePort + ". Discarding it.");
					continue;
				}
				
				// Request parsed successfully
				logger.info(this.bank.getTextId() + " received loan request from " + this.bank.udpAddress.toString() + " for user " + req.emailAddress);
				
				//
				// RESPONDER
				//

	            // Get the sum of all loans for this user and create the response
				int loanSum = this.bank.getLoanSum(req.emailAddress);
	            MessageResponseLoan resp = new MessageResponseLoan();
	            //resp.sequenceNbr = req.sequenceNbr;
	            resp.amountAvailable = loanSum;
				
	            // Prep the response
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ObjectOutputStream oos = new ObjectOutputStream(baos);
		        oos.writeObject(resp);
				sendData = baos.toByteArray();
				baos.close();
	            oos.close();

				logger.info(this.bank.getTextId() + " responding to loan request for user " + req.emailAddress + " with loan sum: " + resp.amountAvailable);

				final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
				serverSocket.send(sendPacket);
			}

		} catch (final SocketException e) {
			logger.info("Unable to bind " + this.bank.getId() + " to UDP Port " + this.bank.udpAddress.getPort() + ". Port already in use.");
			System.exit(1);
		} catch (final IOException e) {
			e.printStackTrace();
			//System.exit(1);
		} finally {if(serverSocket != null) serverSocket.close();}

	}

	
	
//	/**
//	 * Plain text version
//	 */
//	@Override
//	public void run() {
//		
//		DatagramSocket serverSocket = null;
//		
//		try {
//
//			serverSocket = new DatagramSocket(this.bank.udpAddress);
//			byte[] receiveData = new byte[1024];
//			byte[] sendData = new byte[1024];
//
//			while (true) {
//
//				receiveData = new byte[1024];
//				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//
//				// Wait for the packet
//				logger.info(this.bank.getTextId() + " waiting for loan info request packet on " + this.bank.udpAddress.toString());
//				serverSocket.receive(receivePacket);
//				
//				// Parse the packet
//				byte[] data = new byte[receivePacket.getLength()];
//		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
//				final String sentence = new String(data);
//				final InetAddress remoteIpAddress = receivePacket.getAddress();
//				final int remotePort = receivePacket.getPort();
//
//				logger.info(this.bank.getTextId() + " received loan request from " + this.bank.udpAddress.toString() + " with message: " + sentence);
//				//int loanSum = this.bank.getLoanSum(req.emailAddress);
//				
//				final String capitalizedSentence = sentence.toUpperCase();
//				sendData = capitalizedSentence.getBytes();
//
//				logger.info(this.bank.getTextId() + " responding to loan request with message " + capitalizedSentence);
//
//				final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
//				serverSocket.send(sendPacket);
//			}
//
//		} catch (final SocketException e) {
//			logger.info("Unable to bind " + this.bank.getId() + " to UDP Port " + this.bank.udpAddress.getPort() + ". Port already in use.");
//			System.exit(1);
//		} catch (final IOException e) {
//			e.printStackTrace();
//			//System.exit(1);
//		} finally {if(serverSocket != null) serverSocket.close();}
//
//	}

	
	public void runs() {
		
		DatagramSocket aSocket = null;
		
		try {
			
			// Set up a new datagram socket for this listener and initialize data structures
			aSocket = new DatagramSocket(this.bank.udpAddress);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			
			logger.info(this.bank.getTextId() + " waiting for datagram packet on " + this.bank.udpAddress.toString());

			// Loop listener
			while (true) {

				final DatagramPacket receivedPacket;
				final InetAddress remoteIpAddress;
				final int remotePort;
				
				// Wait for and receive a packet
				receiveData = new byte[1024];
				receivedPacket = new DatagramPacket(receiveData, receiveData.length);
				aSocket.receive(receivedPacket);
				
				// Compute the address of the receiver
				remoteIpAddress = receivedPacket.getAddress();
				remotePort = receivedPacket.getPort();
				logger.info(this.bank.getTextId() + " received packet from: " + remoteIpAddress + ":" + remotePort);
				
				// Extract the receiver's message
				ByteArrayInputStream bais = new ByteArrayInputStream(receivedPacket.getData());
	            ObjectInputStream ois = new ObjectInputStream(bais);
	            Object obj;
				try {
					obj = ois.readObject();
				} catch (ClassNotFoundException e) {
					logger.info(this.bank.getTextId() + " an invalid packet from: " + remoteIpAddress + ":" + remotePort + ". Discarding it.");
					continue;
				}
	            bais.close();
	            ois.close();
	            MessageRequestLoan req = null;
	            
				if (obj instanceof MessageRequestLoan) {
					req = (MessageRequestLoan) obj;
				} else {
					logger.info(this.bank.getTextId() + " received an unknown request packet from " + remoteIpAddress + ":" + remotePort + ". Discarding it.");
					continue;
				}

	            // Get the sum of all loans for this user and create the response
				int loanSum = this.bank.getLoanSum(req.emailAddress);
	            MessageResponseLoan resp = new MessageResponseLoan();
	            resp.sequenceNbr = req.sequenceNbr;
	            resp.amountAvailable = loanSum;

				logger.info(this.bank.getTextId() + " received loan info request from " + remoteIpAddress + ":" + remotePort + " for user " + req.emailAddress);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ObjectOutputStream oos = new ObjectOutputStream(baos);
		        oos.writeObject(resp);
				sendData = baos.toByteArray();
				baos.close();
	            oos.close();

				logger.info(this.bank.getTextId() + " responding to loan request for user " + req.emailAddress + " with seNbr: " + resp.sequenceNbr + " loan sum: " + resp.amountAvailable);
				final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
				aSocket.send(sendPacket);
			}

		} catch (final SocketException ex) {
			logger.info("Unable to bind " + this.bank.getId() + " to UDP Port " + this.bank.udpAddress.getPort() + ". Port already in use.");
			System.exit(1);
		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {if(aSocket != null) aSocket.close();}
	}

	
	

//	@Override
//	public void run() {
//		
//		//throw new Exception();
//		
//		DatagramSocket aSocket = null;
//		
//		try {
//			aSocket = new DatagramSocket(this.bank.udpAddress);
//			byte[] receiveData;
//			byte[] sendData = new byte[1024];
//
//			while (true) {
//
//				final DatagramPacket receivedPacket;
//				final InetAddress remoteIpAddress;
//				final DatagramPacket sendPacket;
//				final int remotePort;
//				final String sentence;
//				final String capitalizedSentence;
//				
//				// Wait for and receive a packet
//				receiveData = new byte[1024];
//				receivedPacket = new DatagramPacket(receiveData, receiveData.length);
//				System.out.println("Bank " + this.bank.getId() + " waiting for datagram packet on " + this.bank.udpAddress.toString());
//				aSocket.receive(receivedPacket);
//				
//				// Compute the address of the receiver
//				remoteIpAddress = receivedPacket.getAddress();
//				remotePort = receivedPacket.getPort();
//				System.out.println("Bank " + this.bank.getId() + " received packet from: " + remoteIpAddress + ":" + remotePort);
//				
//				// Extract the receiver's message
//				sentence = new String(receivedPacket.getData());
//				System.out.println("Bank " + this.bank.getId() + " received message: " + sentence);
//				
//				// Process the message and send it back to the peer
//				capitalizedSentence = sentence.toUpperCase();
//				sendData = capitalizedSentence.getBytes();
//				sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
//				aSocket.send(sendPacket);
//			}
//
//		} catch (final SocketException ex) {
//			System.out.println("Unable to bind " + this.bank.getId() + " to UDP Port " + this.port + ". Port already in use.");
//			System.exit(1);
//		} catch (final IOException e) {
//			e.printStackTrace();
//		} finally {if(aSocket != null) aSocket.close();}
//	}
	
	
	
}
