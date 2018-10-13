package model.enums;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.BytesSerializable;
import model.errors.InvalidFormatException;
import utils.Conversion;

public enum QType{
	/** A request for a transfer of an entire zone */
	AXFR(252),
	/** A request for mailbox-related records (MB, MG or MR) */
	MAILB(253),
	/** A request for mail agent RRs (Obsolete - see MX) */
	MAILA(254),
	/** ('*') 255 A request for all records */
	ALL(255);
	public final int value;

	private QType(int value) {
		this.value = value;
	}
	public List<Byte> toBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(2).putShort((short)this.value);
		ArrayList<Byte> result = new ArrayList<>();
		for(byte b : buffer.array()) {
			result.add(b);
		}
		return result;
	}
	public static QType fromBytes(byte byte1, byte byte2) throws InvalidFormatException {
		String concat = Conversion.binaryString(byte1) + Conversion.binaryString(byte2);
		int value = Integer.parseInt(concat, 2);
		switch(value) {
			case 252:
				return AXFR;
			case 253:
				return MAILB;
			case 254:
				return MAILA;
			case 255:
				return ALL;
			default:
				throw new InvalidFormatException("Unrecognized QType value: '"+ concat + "'.");
		}
	}
}
