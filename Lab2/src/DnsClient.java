import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import model.Message;
import utils.Conversion;

public class DnsClient {
	// Fields.


	// Methods.
	public static void main(String[] args) {
		// Program must be invoked according to following format:
		// java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name

		// First thing we do is get input data set up properly and stored.
		InputData inputData = new InputData(args);
		try {
			sendMessage(inputData);
		} catch (IOException e) {
			// TODO: Think about what should happen here.
		}

	}

	private static void sendMessage(InputData inputData) throws IOException {
		// DatagramSockets are Java's way of performing network communication over UDP.
		DatagramSocket clientSocket = new DatagramSocket(); // Create Datagram Socket.
		clientSocket.setSoTimeout(inputData.timeout * 1000); // Set timeout.

		Message queryMessage = new Message(); // Create message object to send. TODO: Message constructor still needs to be made.

		byte[] dnsServerIpAddressInBytes = Conversion.ipAddressStringToByteArray(inputData.dnsServerIp);
		InetAddress dnsServerIp = InetAddress.getByAddress(dnsServerIpAddressInBytes); // Get DNS server IP address object.

		// Need to create datagram packet object to send queryMessage via datagram socket.
		int dnsServerPort = inputData.port;
		byte[] queryMessageInBytes = queryMessage.toByteArray();
		DatagramPacket udpPacket = new DatagramPacket(queryMessageInBytes, queryMessageInBytes.length, dnsServerIp, dnsServerPort);

		// Send packet to DNS server.
		clientSocket.send(udpPacket);

		// Set up byte array and packet object to receive response.
		byte[] responseInBytes = new byte[512]; // Set up receiving byte array for the data.
		DatagramPacket responsePacket = new DatagramPacket(responseInBytes, responseInBytes.length); // Create packet to receive response.

		// Attempt to receive response from DNS server, keeping in mind the number of max retries that got set.
		boolean didReceiveResponse = false;
		for(int i = 0; i < inputData.maxRetries && !didReceiveResponse; i++) {
			try {
				clientSocket.receive(responsePacket); // Receive response. Will block until timeout occurs.
				didReceiveResponse = true;
			} catch (SocketTimeoutException t) {
				System.out.println("Timeout " + i + "/" + inputData.maxRetries);
			}
		}
		// Close socket.
		clientSocket.close();
		
		// Check whether we received data or not. If we did, parse it. Otherwise, throw an exception and display error message.		
		if(didReceiveResponse) {
			parseResponsePacket(responsePacket);
		} else {
			throw new SocketTimeoutException("Exceeded maximum number of retries.");
		}
		
	}
	
	private static Message parseResponsePacket(DatagramPacket responsePacket) {
		byte[] responseBytes = responsePacket.getData();
		Message responseMessage = new Message();
		responseMessage.fromBytes(responseBytes);
		return responseMessage;
		
	}

	

}
