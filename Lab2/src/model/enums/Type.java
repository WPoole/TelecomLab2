package model.enums;

import java.nio.ByteBuffer;

import model.errors.InvalidFormatException;
import utils.Conversion;

public enum Type {
	/** a host address */
	A(1),
	/** an authoritative name server */
	NS(2),
	/** a mail destination (Obsolete - use MX) */
	MD(3),
	/** a mail forwarder (Obsolete - use MX) */
	MF(4),
	/** the canonical name for an alias */
	CNAME(5),
	/** marks the start of a zone of authority */
	SOA(6),
	/** a mailbox domain name (EXPERIMENTAL) */
	MB(7),
	/** a mail group member (EXPERIMENTAL) */
	MG(8),
	/** a mail rename domain name (EXPERIMENTAL) */
	MR(9),
	/** a null RR (EXPERIMENTAL) */
	NULL(10),
	/** a well known service description */
	WKS(11),
	/** a domain name pointer */
	PTR(12),
	/** host information */
	HINFO(13),
	/** mailbox or mail list information */
	MINFO(14),
	/** mail exchange */
	MX(15),
	/** text strings */
	TXT(16);

	public final int value;

	private Type(int value) {
		this.value = value;
	}
	
	public short getValue() {
		return (short) this.value;
	}
	
	public byte[] toBytes() {
		return ByteBuffer.allocate(2).putShort((short) this.value).array();		
	}

	public static Type fromBytes(byte byte1, byte byte2) throws InvalidFormatException {
		String concat = Conversion.binaryString(byte1) + Conversion.binaryString(byte2);
		int value = Integer.parseInt(concat, 2);
		switch (value) {
		case 1:
			return A;
		case 2:
			return NS;
		case 3:
			return MD;
		case 4:
			return MF;
		case 5:
			return CNAME;
		case 6:
			return SOA;
		case 7:
			return MB;
		case 8:
			return MG;
		case 9:
			return MR;
		case 10:
			return NULL;
		case 11:
			return WKS;
		case 12:
			return PTR;
		case 13:
			return HINFO;
		case 14:
			return MINFO;
		case 15:
			return MX;
		case 16:
			return TXT;
		default:
			throw new InvalidFormatException("Unrecognized value: '" + concat + "'.");
		}
	}
}
