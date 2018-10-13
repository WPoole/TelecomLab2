package model.records;

import model.enums.Type;

public class ARecord extends ResourceRecord {
	byte[] address;
	
	public ARecord() {
		this.TYPE = Type.A;
	}
	
	
	@Override
	void parseRData() {
		assert this.RDLENGTH == 4;
		this.address = this.RDATA;
	}

	@Override
	void printToConsole() {
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
		output.append("[TODO: Auth | no-auth]");
		System.out.println(output.toString());
	}
}