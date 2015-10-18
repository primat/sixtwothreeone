package ca.primat.comp6231;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ca.primat.comp6231.response.GetLoanResponse;
import ca.primat.comp6231.response.OpenAccountResponse;
import ca.primat.comp6231.response.ServerResponse;

/**
 * BankServer is the object which handles client and manager operations on Banks
 * 
 * @author mat
 * 
 */
public class BankServer implements BankServerCustomerInterface, BankServerManagerInterface {

	protected volatile Object lockObject;
	protected volatile Bank bank;
	protected volatile HashMap<String, Bank> bankCollection;
	protected int sequenceNbr = 1;
	
	/**
	 * Constructor - Use inversion of control so that we manage creation of dependencies outside this class
	 * 
	 * @param reg The local registry where BankServers register to make themselves available to clients
	 * @param bankCollection The collection of all banks available in the system
	 * @param bankId The bank ID of the bank that this server is managing
	 */
	public BankServer(HashMap<String, Bank> bankCollection, String bankId, final Object lockObject) {
		
		super();
		this.bankCollection = bankCollection;
		this.bank = bankCollection.get(bankId);
		this.lockObject = lockObject;
		
		BankUdpListener udpPeer = new BankUdpListener(this.bank);
		Thread udpPeerThread = new Thread(udpPeer);
		udpPeerThread.start();
	}
	 
	//
	// Operation performed by Customers
	//

	@Override
	public GetLoanResponse getLoan(int accountNbr, String password, int loanAmount) throws RemoteException {

		// Validate that the account exists
		Account account = this.bank.authenticateAccount(accountNbr, password);
		if (account == null) {
			return new GetLoanResponse(false, "", "Account " + accountNbr + " does not exist at bank " + this.bank.getId(), 0);
		}

		// Validate that passwords match
		if (account.getPassword() != password) {
			return new GetLoanResponse(false, "", "Invalid credentials. Loan refused at bank " + this.bank.getId(), 0);
		}
		
		// Avoid making UDP requests if the loan amount is already bigger than the credit limit of the local account
		int currentLoanAmount = this.bank.getLoanSum(accountNbr);
		if (currentLoanAmount +  loanAmount > account.getCreditLimit()) {
			return new GetLoanResponse(false, "", "Loan refused at bank " + this.bank.getId() + ". Local credit limit exceeded", 0);
		}
		
		// Get the loan sum for all banks and approve or not the new loan
		ExecutorService pool = Executors.newFixedThreadPool(this.bankCollection.size()-1);
	    Set<Future<LoanRequestStatus>> set = new HashSet<Future<LoanRequestStatus>>();
	    for (Bank destinationBank : this.bankCollection.values()) {
	    	if (this.bank != destinationBank) {
				Callable<LoanRequestStatus> callable = new UdpRequesterCallable(this.bank, destinationBank, account.emailAddress, this.sequenceNbr);
				Future<LoanRequestStatus> future = pool.submit(callable);
				set.add(future);
			}
		}

		int extLoanSum = 0;
		for (Future<LoanRequestStatus> future : set) {
			
			try {
				LoanRequestStatus status = future.get();
				if (status.status == LoanRequestStatus.STATUS_SUCCESS) {
					extLoanSum += status.loanSum;
				}
				else {
					System.out.println(status.message);
					return new GetLoanResponse(false, "", status.message, 0);
				}
			} catch (InterruptedException e) {
				System.out.println("Bank " + this.bank.getId() + " loan request failed. InterruptedException");
				e.printStackTrace();
				return new GetLoanResponse(false, "", "Bank " + this.bank.getId() + " loan request failed for user " + account.emailAddress + ". InterruptedException", 0);
			} catch (ExecutionException e) {
				System.out.println("Bank " + this.bank.getId() + " loan request failed. ExecutionException");
				e.printStackTrace();
				return new GetLoanResponse(false, "", "Bank " + this.bank.getId() + " loan request failed for user " + account.emailAddress + ". ExecutionException", 0);
			}
		}
		this.sequenceNbr++;
		
		// Check if all operations were successful
		if (currentLoanAmount + extLoanSum > account.getCreditLimit()) {
			return new GetLoanResponse(false, "", "Loan refused at bank " + this.bank.getId() + ". Total credit limit exceeded", 0);
		}
	
		this.bank.createLoan(account.emailAddress, accountNbr, loanAmount);

		return new GetLoanResponse(true, "", "Loan approved at bank " + this.bank.getId() + ".", 0);
	}
	
	@Override
	public OpenAccountResponse openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws RemoteException {

		return this.bank.createAccount(firstName, lastName, emailAddress, phoneNumber, password);
	}

	//
	// Operation performed by Managers
	//
	
	@Override
	public ServerResponse delayPayment(int loanId, Date currentDueDate, Date newDueDate) throws RemoteException {

		Loan loan = this.bank.getLoanById(loanId);
		if (loan == null) {
			return new ServerResponse(false, "", "Loan id " + loanId + " does not exist");
		}
		if (!loan.dueDate.equals(currentDueDate)) {
			return new ServerResponse(false, "", "Loan id " + loanId + " - currentDate argument mismatch");
		}
		if (!loan.dueDate.before(currentDueDate)) {
			return new ServerResponse(false, "", "Loan id " + loanId + " - currentDueDate argument must be later than the actual current due date of the loan");
		}
		
		loan.setDueDate(newDueDate);
		
		return new ServerResponse(true, "", "Loan successfully delayed");
	}

	@Override
	public String printCustomerInfo() throws RemoteException {
		
		for (String key : this.bank.accounts.keySet()) {
			ThreadSafeHashMap<Integer, Account> accountsByLetter = this.bank.accounts.get(key);
			for (Integer accountId : accountsByLetter.keySet()) {
				Account account = accountsByLetter.get(accountId);
				System.out.println(accountId + ": " + account.toString());
				System.out.println("------------------------------------");
			}
		}
		
		return null;
	}
	
	/**
	 * Get the addresses of banks other than this one
	 * @return
	 */
	protected Set<InetSocketAddress> getPeerAddresses() {
		
		Set<InetSocketAddress> peerAddresses = new HashSet<InetSocketAddress>();
		
		for(Bank bankObj : bankCollection.values()) {
		    if (bankObj.udpAddress != this.bank.udpAddress) {
		    	peerAddresses.add(bankObj.udpAddress);
		    }
		}
		
		return peerAddresses;
	}

	
	
//	public int requestLoanAmount(Bank destinationBank, String emailAddress) {
//		//this.bankSocket, this.bank, destinationBank, account.emailAddress, this.sequenceNbr
//		try {
//	
//			final byte[] receiveData = new byte[1024];
//			final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//			
//			MessageRequestLoan message = new MessageRequestLoan();
//			message.emailAddress = emailAddress;
//			message.sequenceNbr = this.sequenceNbr;
//			
//			
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	        ObjectOutputStream oos = new ObjectOutputStream(baos);
//	        oos.writeObject(message);
//			byte[] sendData = baos.toByteArray();
//			
//			//byte[] sendData = new byte[1024];
//			//sendData = emailAddress.getBytes();
//			
//			System.out.println("Bank " + this.bank.getId() + " requesting loan info from bank " + destinationBank.getId() + " (" + sendData.length + " bytes)");
//			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationBank.udpAddress);
//			this.hostSocket.send(sendPacket);
//			this.hostSocket.setSoTimeout(3000);
//			
//			try {
//				ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
//	            ObjectInputStream ois = new ObjectInputStream(bais);
//	            Object obj;
//				obj = ois.readObject();
//	            bais.close();
//	            ois.close();
//	            
//	            MessageResponseLoan resp = null;
//	            
//	            if(obj instanceof MessageResponseLoan) {
//	                System.out.println("Loan Request");
//	                resp = (MessageResponseLoan) obj;
//	            } else {
//	                System.out.println("Not a loan request");
//	                return -1;
//	            }
//	            
//	            if (resp.sequenceNbr != this.sequenceNbr) {
//	            	System.out.println("Bank " + this.sourceBank.getId() + " received an out of sequence response from BankServer " + this.destinationBank.getId() + ". Discarding packet.");
//	                return -1;
//	            }
//	            
//                System.out.println(("Bank " + this.sourceBank.getId() + " received a successfull response from BankServer " + this.destinationBank.getId() + " (available amount: " + resp.amountAvailable + ")"));
//				
//				
////				clientSocket.receive(receivePacket);
////				final String modifiedSentence = new String(receivePacket.getData());
////				final InetAddress returnIPAddress = receivePacket.getAddress();
////				final int port = receivePacket.getPort();
////				System.out.println("From server at: " + returnIPAddress + ":" + port);
////				System.out.println("Message: " + modifiedSentence);
////				
////				
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (final SocketTimeoutException ste) {
//				System.out.println("Timeout Occurred: Packet assumed lost");
//			}
//			clientSocket.close();
//
//		} catch (final SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (final IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return 100;
//	}
	
	
}
