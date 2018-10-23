package model.records;

import java.nio.ByteBuffer;
import java.util.Arrays;

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
			// the exchange field is two bytes further within RDATA, since we just parsed the PREFERENCE field (2 bytes)
			this.exchange = DomainName.parseDomainName(rawBytes, rDataOffset + 2).result;
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
		output.append(this.exchange);
		output.append('\t');
		output.append(this.preference);
		output.append('\t');
		output.append(this.TTL);
		output.append('\t');
		output.append(this.isAuthoritative ? "Auth" : "no-auth");
		return output.toString();
	}

}