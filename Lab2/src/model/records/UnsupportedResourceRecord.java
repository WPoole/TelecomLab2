package model.records;

import model.enums.Type;

public class UnsupportedResourceRecord extends ResourceRecord {
	
	public UnsupportedResourceRecord(Type type) {
		this.TYPE = type;
	}
	
	@Override
	protected void parseRData(byte[] rawBytes, int rDataOffset) {
	}

	@Override
	protected String consoleString() {
		return this.TYPE.name() + "\t" + this.nameString;
	}
}