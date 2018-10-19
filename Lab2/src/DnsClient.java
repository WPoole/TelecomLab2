import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import model.Message;
import model.errors.InvalidFormatException;
import model.records.ResourceRecord;

public class DnsClient {
	public static void main(String[] args) {
//		args = new String[] { "–t", "10", "–r", "2", "–mx", "@8.8.8.8", "mcgill.ca" };
//		args = new String[] { "–mx", "@8.8.8.8", "yahoo.ca" };
//		args = new String[] { "@8.8.8.8", "yahoo.ca" };
//		args = new String[] { "@8.8.8.8", "yahoo.ca" };
		args = new String[] { "@8.8.8.8", "www.mcgill.ca" };

		// Program must be invoked according to following format:
		// java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name

		try {
			Message response = performDNSRequest(args);
			response.printToConsole();

		} catch (Exception e) {
			System.err.println("ERROR\t"+e);
			e.printStackTrace();
		}
	}
	
	public static Message performDNSRequest(String[] args) throws IOException, InvalidFormatException {
		// First thing we do is get input data set up properly and stored.
		InputData input = new InputData(args);
		System.out.println("DnsClient sending request for " + input.name);
		System.out.println("Server: " + input.dnsServerIpString);
		System.out.println("Request type: " + input.type.name());
		
		// Create the request Message instance.
		Message m = new Message(input.type, input.dnsServerIp, input.name);
		byte[] data = m.toByteArray();

		// Need to create datagram packet object to send queryMessage via datagram
		// socket.
		InetAddress dnsAddress = InetAddress.getByAddress(input.dnsServerIp);
		DatagramPacket udpPacket = new DatagramPacket(data, data.length, dnsAddress, input.port);
		DatagramSocket socket = new DatagramSocket(); // Create Datagram Socket.
		socket.setSoTimeout(input.timeout * 1000); // Set timeout. // Get DNS server IP address object.

		// Send packet to DNS server.
		long startTime = System.currentTimeMillis();

		socket.send(udpPacket);

		// Create packet to receive the response.
		byte[] responseInBytes = new byte[512];
		DatagramPacket responsePacket = new DatagramPacket(responseInBytes, responseInBytes.length);

		// Attempt to receive response from DNS server, keeping in mind the number of
		// max retries.
		boolean didReceiveResponse = false;
		int retries = 0;
		for (; retries < input.maxRetries && !didReceiveResponse; retries++) {
			try {
				socket.receive(responsePacket); // Receive response. Will block until timeout occurs.
				didReceiveResponse = true;
				float timeSpent = (System.currentTimeMillis() - startTime) / 1000f;
				System.out.println("Response received after " + String.format("%.3f", timeSpent) + " seconds (" + retries + " retries)");
			} catch (SocketTimeoutException t) {
				System.out.println("Timeout " + retries + "/" + input.maxRetries);
			}
		}

		// Close socket.
		socket.close();

		if (!didReceiveResponse) {
			throw new SocketTimeoutException("Exceeded maximum number of retries.");
		}
		byte[] responseBytes = responsePacket.getData();
		Message response = Message.fromBytes(responseBytes);
		return response;
	}
}
