package ca.primat.comp6231;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class BankUdpListener implements Runnable {

	protected int port;
	protected Bank bank;
	
	/**
	 * Constructor - Run the UDP Listener
	 * 
	 * @param port
	 * @param bank
	 */
	BankUdpListener(Bank bank) {

		this.bank = bank;
	}

	@Override
	public void run() {
		
		//throw new Exception();
		
		DatagramSocket aSocket = null;
		
		try {
			aSocket = new DatagramSocket(this.bank.udpAddress);
			byte[] receiveData;
			byte[] sendData = new byte[1024];

			while (true) {

				final DatagramPacket receivedPacket;
				final InetAddress remoteIpAddress;
				final DatagramPacket sendPacket;
				final int remotePort;
				final String sentence;
				final String capitalizedSentence;
				
				// Wait for and receive a packet
				receiveData = new byte[1024];
				receivedPacket = new DatagramPacket(receiveData, receiveData.length);
				System.out.println("Bank " + this.bank.getId() + " waiting for datagram packet on " + this.bank.udpAddress.toString());
				aSocket.receive(receivedPacket);
				
				// Compute the address of the receiver
				remoteIpAddress = receivedPacket.getAddress();
				remotePort = receivedPacket.getPort();
				System.out.println("Bank " + this.bank.getId() + " received packet from: " + remoteIpAddress + ":" + remotePort);
				
				// Extract the receiver's message
				sentence = new String(receivedPacket.getData());
				System.out.println("Bank " + this.bank.getId() + " received message: " + sentence);
				
				// Process the message and send it back to the peer
				capitalizedSentence = sentence.toUpperCase();
				sendData = capitalizedSentence.getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
				aSocket.send(sendPacket);
			}

		} catch (final SocketException ex) {
			System.out.println("Unable to bind " + this.bank.getId() + " to UDP Port " + this.port + ". Port already in use.");
			System.exit(1);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {if(aSocket != null) aSocket.close();}
	}
}
