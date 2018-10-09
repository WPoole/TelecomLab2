package model;

import utils.Conversion;

public class Pointer {
	public short offset;
	public Pointer(short offset) {
		this.offset = offset;
	}
	public Pointer(byte byte0, byte byte1) {
		String stringValue = Conversion.binaryString(byte0) + Conversion.binaryString(byte1);
		if(!stringValue.substring(0,2).equals("11")) {
			throw new IllegalArgumentException("The first two bits of a Pointer must be ones.");
		}
		offset = Short.parseShort(stringValue.substring(2, 16), 2);
	}
	public byte[] toBytes() {
		return new byte[] {
			(byte)(0b11000000 | (offset << 8)),
			(byte)(0xFF & offset),
		};
	}
}
