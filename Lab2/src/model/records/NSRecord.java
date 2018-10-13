package model.records;

import model.enums.Type;
import model.errors.InvalidFormatException;
import utils.DomainName;

public class NSRecord extends ResourceRecord {
	/**
	 * A <domain-name> which specifies a host which should be authoritative for the
	 * specified class and domain.
	 */
	String nameServerDomainName;

	public NSRecord() {
		this.TYPE = Type.NS;
	}
	
	@Override
	protected void parseRData() {
		try {
			this.nameServerDomainName = DomainName.parseDomainName(this.RDATA).result;
		} catch (InvalidFormatException e) {
			System.err.println("ERROR\t Unable to parse the name server domain name inside the Resource Record.");
			e.printStackTrace();
		}
	}

	@Override
	protected String consoleString() {
		// NS * <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth]
		StringBuilder output = new StringBuilder();
		output.append("NS\t");
		output.append(this.nameString);
		output.append('\t');
		output.append(this.TTL);
		output.append('\t');
		output.append("[TODO: Auth | no-auth]");
		return output.toString();
	}
}