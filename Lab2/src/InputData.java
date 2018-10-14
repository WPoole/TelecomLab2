import model.enums.Type;
import utils.Conversion;

public class InputData {

	// Fields.
	// All default field values were specified in assignment.
	int timeout = 5; // Gives how long to wait, in seconds, before retransmitting an unanswered query.
	int maxRetries = 3; // Max number of times to retransmit an unanswered query before giving up.
	int port = 53; // UDP port number of the DNS server.
	Type type = Type.NONE; // Indicates what type of query to send to the DNS server (MX, NS, or A).

	// These both need to be set.
	byte[] dnsServerIp; // IP address of DNS server we are contacting.
	String dnsServerIpString;
	String name; // The domain name we are querying for.

	// Constructor.
	InputData(String[] args) {
		// Set all initial values.
		setTimeout(args);
		setMaxRetries(args);
		setPort(args);
		setType(args);
		setServer(args);
		setName(args);
	}

	// Methods.
	// Main setters.
	private void setTimeout(String[] args) {
		// We only go to args.length - 1 to avoid index out of bounds exceptions. This is fine because
		// The last argument should be the name anyways which is unrelated to other input arguments.
		for(int i = 0; i < args.length - 1; i++) {
			if(args[i].equals("-t")) {
				if(InputData.isInteger(args[i+1])) {
					this.timeout = Integer.parseInt(args[i+1]);
				} else {
					throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - The " + args[i] + " flag was followed by an incorrect argument.");
				}
			}
		}
		// If no -t flag is found we set timeout field to default value specified in assignment.
		this.timeout = 5;
	}

	private void setMaxRetries(String[] args) {
		// We only go to args.length - 1 to avoid index out of bounds exceptions. This is fine because
		// The last argument should be the name anyways which is unrelated to other input arguments.
		for(int i = 0; i < args.length - 1; i++) {
			if(args[i].equals("-r")) {
				if(InputData.isInteger(args[i+1])) {
					this.maxRetries = Integer.parseInt(args[i+1]);
				} else {
					throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - The " + args[i] + " flag was followed by an incorrect argument.");
				}
			}
		}

		// If no -r flag is found we set maxRetries field to default value specified in assignment.
		this.maxRetries = 3;
	}

	private void setPort(String[] args) {
		// We only go to args.length - 1 to avoid index out of bounds exceptions. This is fine because
		// The last argument should be the name anyways which is unrelated to other input arguments.
		for(int i = 0; i < args.length - 1; i++) {
			if(args[i].equals("-p")) {
				if(InputData.isInteger(args[i+1])) {
					this.port = Integer.parseInt(args[i+1]);
				} else {
					throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - The " + args[i] + " flag was followed by an incorrect argument.");
				}
			}
		}

		// If no -p flag is found we set port field to default value specified in assignment.
		this.port = 53;
	}

	private void setType(String[] args) {
		// We only go to args.length - 1 to avoid index out of bounds exceptions. This is fine because
		// The last argument should be the name anyways which is unrelated to other input arguments.
		for(int i = 0; i < args.length - 1; i++) {

			if(args[i].equals("-mx")) {
				// If the type is already set, then we know there must be duplicate type flags in the input,
				// which is not allowed.
				if(this.type != Type.NONE) {
					throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - There cannot be more than one type flag in the input.");
				}

				this.type = Type.MX;

			} else if(args[i].equals("-ns")) {
				if(this.type != Type.NONE) {
					throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - There cannot be more than one type flag in the input.");
				}
				this.type = Type.NS;
			}
		}
		
		// If no -mx or -ns flag is found we set type field to default value specified in assignment.
		if(this.type == Type.NONE) {
			this.type = Type.A;
		}
	}

	private void setServer(String[] args) {
		// We only go to args.length - 1 to avoid index out of bounds exceptions. This is fine because
		// The last argument should be the name anyways which is unrelated to other input arguments.
		for(int i = 0; i < args.length - 1; i++) {
			if(args[i].charAt(0) == '@') {
				// If the server IP address has already been set, then we know that more than one server IP 
				// address argument was entered, which is not allowed.
				if(this.dnsServerIp != null) {
					throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - There cannot be more than one server IP address in the input.");
				}

				String serverIp = args[i].substring(1);
				// Need to check if this is a valid IP address format.
				if(isValidIpFormat(serverIp)) {
					this.dnsServerIpString = serverIp;
					this.dnsServerIp = Conversion.ipAddressStringToByteArray(serverIp);
					return;
				} else {
					throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - Please enter a valid server IP address. (received "+ serverIp + ")");
				}
			}
		}
		// If we get to here we know they did not input a server IP address using the @ character.
		throw new IllegalArgumentException("ERROR\t Incorrect Input Syntax - Please enter a valid server IP address starting with the '@' character.");
	}

	private void setName(String[] args) {
		this.name = args[args.length - 1];
	}

	// Helpers.
	public static boolean isInteger(String input) {
		try { 
			Integer.parseInt(input); 
		} catch(NumberFormatException e) { 
			return false; 
		} catch(NullPointerException e) {
			return false;
		}

		return true;
	}

	public static boolean isValidIpFormat(String serverIp) {
		String[] ipComponents = serverIp.split("\\.");
		if(ipComponents.length != 4) { // There should be 4 numbers in each IP address.
			return false;
		}
		for(int i = 0; i < ipComponents.length; i++) {
			if(!isInteger(ipComponents[i])) {
				return false;
			}
			int value = Integer.parseInt(ipComponents[i]);
			if(value > 0xFF) {
				return false;
			}
		}
		// If there are 4 integer values that are separated by periods, it has the correct format
		// of an IP address.
		return true;
	}
}
