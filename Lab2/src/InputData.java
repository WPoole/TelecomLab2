
public class InputData {

	// Fields.
	// All default field values were specified in assignment.
	int timeout = 5;
	int maxRetries = 3;
	int port = 53;
	String type = "A";

	// These both need to be set.
	String server;
	String name;

	// Constructor.
	InputData(String[] args) {
		// TODO: Set values from input and handle errors.
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
					throw new IllegalArgumentException("Incorrect Input Format - The " + args[i] + "flag was followed by an incorrect argument.");
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
					throw new IllegalArgumentException("Incorrect Input Format - The " + args[i] + "flag was followed by an incorrect argument.");
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
					throw new IllegalArgumentException("Incorrect Input Format - The " + args[i] + "flag was followed by an incorrect argument.");
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
			// If the type is already set, then we know there must be duplicate type flags in the input,
			// which is not allowed.
			if(this.type.equals("MX") || this.type.equals("NS")) {
				throw new IllegalArgumentException("Incorrect Input Format - There cannot be more than one type flag in the input.");
			}
			
			if(args[i].equals("-mx")) {
				this.type = "MX";
				
			} else if(args[i].equals("-ns")) {
				this.type = "NS";
			}
		}
		
		// If no -mx or -ns flag is found we set type field to default value specified in assignment.
		this.type = "A";
	}

	private void setServer(String[] args) {

	}

	private void setName(String[] args) {

	}

	// Helpers.
	private static boolean isInteger(String input) {
		try { 
			Integer.parseInt(input); 
		} catch(NumberFormatException e) { 
			return false; 
		} catch(NullPointerException e) {
			return false;
		}

		return true;
	}
}
