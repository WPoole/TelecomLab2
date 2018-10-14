package model.records;

import java.nio.ByteBuffer;
import model.BytesSerializable;
import model.enums.QClass;
import model.enums.Type;
import model.errors.InvalidFormatException;
import utils.DomainName;
import utils.ParsingResult;

public abstract class ResourceRecord implements BytesSerializable {
	/**
	 * An owner name, i.e., the name of the node to which this resource record
	 * pertains. ("a domain name is terminated by a length byte of zero. The high
	 * order two bits of every length octet must be zero, and the remaining six bits
	 * of the length field limit the label to 63 octets or less."
	 */
	byte[] NAME;
	/**
	 * The human-readable domain name contained within the NAME field (ex: "www.mcgill.ca").
	 */
	String nameString;
	/**
	 * The total byte-length of this RR. Used when reading through a sequence of RR's.
	 */
	int byteLength;
	/**
	 * two octets containing one of the RR type codes. This field specifies the
	 * meaning of the data in the RDATA field.
	 */
	Type TYPE;
	/**
	 * two octets which specify the class of the data in the RDATA field.
	 */
	QClass CLASS;
	/**
	 * a 32 bit unsigned integer that specifies the time interval (in seconds) that
	 * the resource record may be cached before it should be discarded. Zero values
	 * are interpreted to mean that the RR can only be used for the transaction in
	 * progress, and should not be cached.
	 */
	long TTL;
	/**
	 * an unsigned 16 bit integer that specifies the length in octets of the RDATA
	 * field.
	 */
	int RDLENGTH;
	/**
	 * a variable length string of octets that describes the resource. The format of
	 * this information varies according to the TYPE and CLASS of the resource
	 * record. For example, the if the TYPE is A and the CLASS is IN, the RDATA
	 * field is a 4 octet ARPA Internet address.
	 */
	byte[] RDATA;
	
	/**
	 * Is set to true if the Message Header's AA flag is true.
	 */
	boolean isAuthoritative;
	@Override
	public byte[] toByteArray() {
		this.NAME = DomainName.toBytes(this.nameString);
		ByteBuffer buffer = ByteBuffer
			.allocate(this.length())
			.put(this.NAME)
			.putShort((short) this.TYPE.value)
			.putShort((short) this.CLASS.value)
			.putInt((int) this.TTL)
			.putShort((short) this.RDLENGTH)
			.put(this.RDATA);
		return buffer.array();
	}
	
	/**
	 * Return the length (in bytes) of this ResourceRecord.
	 * @return
	 */
	public int length() {
		if (this.NAME == null) {
			this.NAME = DomainName.toBytes(this.nameString);
		}
		return this.NAME.length + 2 // TYPE
			+ 2 // CLASS
			+ 4 // TTL
			+ 2 // RDLENGTH
			+ this.RDLENGTH;
	}

	public static ResourceRecord fromBytes(byte[] bytes, int offset, boolean isAuthoritative) throws InvalidFormatException {
		ResourceRecord rr;
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, bytes.length - offset).asReadOnlyBuffer();
		
		ParsingResult<String> name = DomainName.parseDomainName(bytes, offset);
		byte[] nameBytes = new byte[name.bytesUsed];
		// copy the bytes of the 'NAME' field into the nameBytes array.
		buffer.get(nameBytes);
		
		// parse the TYPE, CLASS, TTL, and RDLength, which are fixed-length fields.
		Type type = Type.fromBytes(buffer.get(), buffer.get());

		switch(type) {
		case A:
			rr = new ARecord();
			break;
		case MX:
			rr = new MXRecord();
			break;
		case NS:
			rr = new NSRecord();
			break;
		case CNAME:
			rr = new CNAMERecord();
			break;
		default:
			rr = null;
			break;
		}
		rr.isAuthoritative = isAuthoritative;
		rr.NAME = nameBytes;
		rr.nameString = name.result;
		
		
		rr.CLASS = QClass.fromBytes(buffer.get(), buffer.get());
		rr.TTL = Integer.toUnsignedLong(buffer.getInt());
		rr.RDLENGTH = buffer.getShort() & 0xFFFF;
		rr.RDATA = new byte[rr.RDLENGTH];

		// copy the next RDLENGTH bytes into the RDATA field.
		buffer.get(rr.RDATA);
		// parse the RDATA section (type-dependent implementation).
		rr.parseRData();
		
		return rr;
	}
	
	public void printToConsole() {
		System.out.println(this.consoleString());
	}
	
	/**
	 * Populates the corresponding fields using the Bytes in the RDATA array.
	 */
	protected abstract void parseRData();
	
	protected abstract String consoleString();
}
