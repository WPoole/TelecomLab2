package model.records;

import model.enums.Type;
import model.errors.InvalidFormatException;
import utils.DomainName;

public class CNAMERecord extends ResourceRecord {
	/**
	 * A domain name which specifies the canonical or primary name for the owner. The owner name is an alias.
	 */
	String cannonicalName;
	
	public CNAMERecord() {
		this.TYPE = Type.CNAME;
	}
	
	@Override
	protected void parseRData() {
		try {
			this.cannonicalName = DomainName.parseDomainName(this.RDATA).result;
		} catch (InvalidFormatException e) {
			System.err.println("ERROR\t Unable to parse the domain name inside the Resource Record.");
			e.printStackTrace();
		}
	}

	@Override
	protected String consoleString() {
		// CNAME <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth]
		StringBuilder output = new StringBuilder();
		output.append("CNAME\t");
		output.append(this.nameString);
		output.append('\t');
		output.append(this.TTL);
		output.append('\t');
		output.append(this.isAuthoritative ? "Auth" : "no-auth");
		return output.toString();
	}
}