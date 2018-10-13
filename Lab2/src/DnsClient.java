import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import model.Message;
import model.errors.InvalidFormatException;
import model.records.ResourceRecord;
import utils.Conversion;

public class DnsClient {
	// Fields.

	// Methods.
	public static void main(String[] args) {
		// Program must be invoked according to following format:
		// java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name

		try {
			// First thing we do is get input data set up properly and stored.
			InputData input = new InputData(args);

			// Create the message
			Message m = new Message(input.type, input.dnsServerIp, input.name);
			byte[] data = m.toByteArray();

			// Need to create datagram packet object to send queryMessage via datagram
			// socket.
			InetAddress dnsAddress = InetAddress.getByAddress(input.dnsServerIp);
			DatagramPacket udpPacket = new DatagramPacket(data, data.length, dnsAddress, input.port);
			DatagramSocket socket = createSocket(input.dnsServerIp, input.port, input.timeout);

			// Send packet to DNS server.
			socket.send(udpPacket);

			// Set up byte array and packet object to receive response.
			// Create packet to receive response.
			byte[] responseInBytes = new byte[512]; // Set up receiving byte array for the data.
			DatagramPacket responsePacket = new DatagramPacket(responseInBytes, responseInBytes.length);

			// Attempt to receive response from DNS server, keeping in mind the number of
			// max retries.
			boolean didReceiveResponse = false;
			int retries = 0;
			for (; retries < input.maxRetries && !didReceiveResponse; retries++) {
				try {
					socket.receive(responsePacket); // Receive response. Will block until timeout occurs.
					didReceiveResponse = true;
				} catch (SocketTimeoutException t) {
					System.out.println("Timeout " + retries + "/" + input.maxRetries);
				}
			}
			
			// Close socket.
			socket.close();

			if (!didReceiveResponse) {
				throw new SocketTimeoutException("ERROR\t Exceeded maximum number of retries.");
			}
			
			System.out.println("Response received after [time] seconds (" + retries + " retries)");
			
			
			Message response = Message.fromBytes(responsePacket.getData());
			for(ResourceRecord rr : response.answer) {
				rr.printToConsole();
			}

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	private static DatagramSocket createSocket(byte[] serverIp, int serverPort, int timeout)
			throws SocketException, UnknownHostException {
		DatagramSocket clientSocket = new DatagramSocket(); // Create Datagram Socket.
		clientSocket.setSoTimeout(timeout * 1000); // Set timeout. // Get DNS server IP address object.
		return clientSocket;
	}

	private static void sendData(DatagramSocket socket, byte[] data, byte[] dnsServerIp, int dnsServerPort) {

	}

	

	private static Message parseResponsePacket(DatagramPacket responsePacket) {
		byte[] responseBytes = responsePacket.getData();
		try {
			Message responseMessage = Message.fromBytes(responseBytes);
			return responseMessage;
		} catch (InvalidFormatException e) {
			System.err.println("ERROR\t Error while parsing the response packet:" + e);
			e.printStackTrace();
			System.exit(1);
		}
		return null;

	}

}
