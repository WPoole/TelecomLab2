import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import model.Message;

public class DnsClient {
	// Fields.
	
	
	// Methods.
	public static void main(String[] args) {
		// Program must be invoked according to following format:
		// java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name
		
		// First thing we do is get input data set up properly and stored.
		InputData inputData = new InputData(args);
		sendMessage(inputData);
		
	}
	
	private void sendMessage(InputData inputData) throws IOException {
		// DatagramSockets are Java's way of performing network communication over UDP.
		DatagramSocket clientSocket = new DatagramSocket(); // Create Datagram Socket.
		clientSocket.setSoTimeout(inputData.timeout * 1000); // Set timeout.
		
		Message queryMessage = new Message(); // Create message object to send. TODO: Message constructor still needs to be made.
		
		byte[] dnsServerIpAddressInBytes = getServerIpAddressInBytes(inputData.server); // Get byte array representation of server IP address.
		InetAddress dnsServerIp = InetAddress.getByAddress(dnsServerIpAddressInBytes); // Get DNS server IP address object.
		
		// Need to create datagram packet object to send queryMessage via datagram socket.
		int dnsServerPort = inputData.port;
		DatagramPacket udpPacket = new DatagramPacket(queryMessage.toByteArray(), queryMessage.getByteLength(), dnsServerIp, dnsServerPort);
		
		// Send packet to DNS server.
		clientSocket.send(udpPacket);
		
	}
	
	private byte[] getServerIpAddressInBytes(String serverIp) {
		String[] serverIpComponents = serverIp.split(".");
		byte[] serverIpAddressInBytes = new byte[serverIpComponents.length];
		for(int i = 0; i < serverIpComponents.length; i++) {
			// Note: The below line should not throw any exceptions since we already did all necessary
			// checks when forming the InputData object.
			serverIpAddressInBytes[i] = (byte) (Integer.parseInt(serverIpComponents[i]));
		}
		
		return serverIpAddressInBytes;
	}

}
