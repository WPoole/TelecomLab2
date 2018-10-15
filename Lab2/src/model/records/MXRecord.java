package model.records;

import java.nio.ByteBuffer;

import model.enums.Type;
import model.errors.InvalidFormatException;
import utils.DomainName;

public class MXRecord extends ResourceRecord {
	/**
	 * A 16 bit integer which specifies the preference given to this RR among others
	 * at the same owner. Lower values are preferred.
	 */
	short preference;
	/**
	 * A <domain-name> which specifies a host willing to act as a mail exchange for
	 * the owner name.
	 */
	String exchange;

	public MXRecord() {
		this.TYPE = Type.MX;
	}
	
	@Override
	protected void parseRData(byte[] rawBytes, int rDataOffset) {
		this.preference = ByteBuffer.wrap(this.RDATA).getShort();
		try {
			this.exchange = DomainName.parseDomainName(rawBytes, rDataOffset).result;
		} catch (InvalidFormatException e) {
			System.err.println("ERROR\t Unable to parse the domain name in the MXRecord.");
			e.printStackTrace();
		}
	}

	@Override
	protected String consoleString() {
		// MX <tab> [alias] <tab> [pref] <tab> [seconds can cache] <tab> [auth | nonauth]
		StringBuilder output = new StringBuilder();
		output.append("MX\t");
		output.append(this.nameString);
		output.append('\t');
		output.append(this.preference);
		output.append('\t');
		output.append(this.TTL);
		output.append('\t');
		output.append(this.isAuthoritative ? "Auth" : "no-auth");
		return output.toString();
	}

}