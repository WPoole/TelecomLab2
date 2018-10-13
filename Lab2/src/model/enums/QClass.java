package model.enums;

import utils.Conversion;

public enum QClass {
	/** the Internet*/
	IN (1),
	/** the CSNET class (Obsolete - used only for examples in
	some obsolete RFCs) */
	CS (2),
	/**
	 * the CHAOS class
	 */
	CH (3),
	/**
	 * Hesiod [Dyer 87]
	 */
	HS (4),
	ALL (255);
	public final int value;
	private QClass(int value){
		this.value = value;
	}
	
	public static QClass fromBytes(byte byte1, byte byte2) {
		String concat = Conversion.binaryString(byte1) + Conversion.binaryString(byte2);
		int value = Integer.parseInt(concat, 2);
		switch(value) {
			case 1:
				return IN;
			case 2:
				return CS;
			case 3:
				return CH;
			case 4:
				return HS;
			case 255:
				return ALL;
			default:
				throw new IllegalArgumentException("Unrecognized QueryClass value");
		}
	}
}