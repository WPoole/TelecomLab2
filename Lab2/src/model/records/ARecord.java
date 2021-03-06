package model.records;

import model.enums.Type;

public class ARecord extends ResourceRecord {
	byte[] address;
	
	public ARecord() {
		this.TYPE = Type.A;
	}
	
	
	@Override
	protected void parseRData(byte[] rawBytes, int rDataOffset) {
		assert this.RDLENGTH == 4;
		this.address = this.RDATA;
	}

	@Override
	protected String consoleString() {
		// IP <tab> [ip address] <tab> [seconds can cache] <tab> [auth | nonauth]
		StringBuilder output = new StringBuilder();
		output.append("IP\t");
		for (int i = 0; i < 4; i++) {
			output.append(this.RDATA[i] & 0xFF);
			if (i != 3) {
				output.append('.');
			}
		}
		output.append('\t');
		output.append(this.TTL);
		output.append('\t');
		output.append(this.isAuthoritative ? "Auth" : "no-auth");
		return output.toString();
	}
}