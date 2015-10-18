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
import java.net.UnknownHostException;
import java.util.Set;

/**
 * 
 * @author mat
 *
 */
public class BankUdpPeer {

	protected int port;
	protected Set<Integer> peerPorts;
	protected InetAddress localIpAddress;
	
	protected int sequenceNbr = 1;
	
	

	/**
	 * Constructor
	 * 
	 * @param port The port number of this listener
	 * @param peerPorts the port numbers of peers from who to make requests
	 */
	public BankUdpPeer(int port, Set<Integer> peerPorts) {
		this.port = port;
		this.peerPorts = peerPorts;
		messageListener();
		try {
			localIpAddress = InetAddress.getLocalHost();
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void requestData(final String data) {
		final byte[] receiveData = new byte[1024];
		DatagramSocket clientSocket = null;
		byte[] sendData = new byte[1024];
		final DatagramPacket receivePacket;
		
		try {
			clientSocket = new DatagramSocket();
			
			// Create and prepare the message
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			sendData = data.getBytes();
			clientSocket.setSoTimeout(10000);
			
			System.out.print("Ready to send data");

			// Loop send the request to all peers
			for (Integer peerPort : this.peerPorts) {
		        //System.out.println(peerPort);
				
				
				// Join all threads
//				for (Thread thread : threads) {
//					  thread.join();
//					}
				
//				ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
//				while(...) {
//				  taskExecutor.execute(new MyTask());
//				}
//				taskExecutor.shutdown();
//				try {
//				  taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//				} catch (InterruptedException e) {
//				  ...
//				}
				
				
				final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, localIpAddress, peerPort);
				clientSocket.send(sendPacket);
				try {
					clientSocket.receive(receivePacket);
					final String modifiedSentence = new String(receivePacket.getData());

					final InetAddress returnIpAddress = receivePacket.getAddress();

					final int port = receivePacket.getPort();

					System.out.println("From server at: " + returnIpAddress + ":" + port);
					System.out.println("Message: " + modifiedSentence);

				} catch (final SocketTimeoutException ste) {
					System.out.println("Timeout Occurred: Packet assumed lost");
				}
		     }
			clientSocket.close();	
			
//			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, localIpAddress, peerPort);
//			clientSocket.send(sendPacket);
//			clientSocket.setSoTimeout(10000);
//			try {
//				clientSocket.receive(receivePacket);
//				final String modifiedSentence = new String(receivePacket.getData());
//
//				final InetAddress returnIpAddress = receivePacket.getAddress();
//
//				final int port = receivePacket.getPort();
//
//				System.out.println("From server at: " + returnIpAddress + ":" + port);
//				System.out.println("Message: " + modifiedSentence);
//
//			} catch (final SocketTimeoutException ste) {
//				System.out.println("Timeout Occurred: Packet assumed lost");
//			}
//			clientSocket.close();

		} catch (final SocketException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {if(clientSocket != null) clientSocket.close();}

	}

	/**
	 * Listens for incoming packets and processes the request
	 */
	protected void messageListener() {
		DatagramSocket aSocket = null;
		
		try {
			aSocket = new DatagramSocket(this.port);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];

			while (true) {

				final DatagramPacket receivedPacket;
				final InetAddress remoteIpAddress;
				final int remotePort;
				
				// Wait for and receive a packet
				receiveData = new byte[1024];
				receivedPacket = new DatagramPacket(receiveData, receiveData.length);
				System.out.println("Waiting for datagram packet");
				aSocket.receive(receivedPacket);
				
				// Compute the address of the receiver
				remoteIpAddress = receivedPacket.getAddress();
				remotePort = receivedPacket.getPort();
				System.out.println("Received packet from: " + remoteIpAddress + ":" + remotePort);
				
				// Extract the receiver's message
				ByteArrayInputStream bais = new ByteArrayInputStream(receivedPacket.getData());
	            ObjectInputStream ois = new ObjectInputStream(bais);
	            Object obj;
				try {
					obj = ois.readObject();
				} catch (ClassNotFoundException e) {
					System.out.println("Received an invalid packet");
					continue;
				}
	            bais.close();
	            ois.close();
	            MessageRequestLoan req = null;
	            
				if (obj instanceof MessageRequestLoan) {
					System.out.println("Loan Request");
					req = (MessageRequestLoan) obj;
				} else {
					System.out.println("Not a loan request");
					continue;
				}

	            MessageResponseLoan resp = new MessageResponseLoan();
	            resp.sequenceNbr = req.sequenceNbr;
	            resp.amountAvailable = 100;

				System.out.println("Received loan request for user : " + req.emailAddress);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ObjectOutputStream oos = new ObjectOutputStream(baos);
		        oos.writeObject(resp);
				sendData = baos.toByteArray();
				baos.close();
	            oos.close();
				
				final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
				aSocket.send(sendPacket);
			}

		} catch (final SocketException ex) {
			System.out.println("UDP Port " + this.port + " is already in use.");
			System.exit(1);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {if(aSocket != null) aSocket.close();}
	}
}
