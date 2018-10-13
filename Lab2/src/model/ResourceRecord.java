package model;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import model.enums.QClass;
import model.enums.Type;
import model.errors.InvalidFormatException;
import utils.Conversion;
import utils.DomainName;
import utils.ParsingResult;

class ResourceRecord implements BytesSerializable {
	/**
	 * An owner name, i.e., the name of the node to which this resource record
	 * pertains. ("a domain name is terminated by a length byte of zero. The high
	 * order two bits of every length octet must be zero, and the remaining six bits
	 * of the length field limit the label to 63 octets or less."
	 */
	byte[] NAME;

	private String nameString;
	private int preference;
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
	int TTL;
	/**
	 * an unsigned 16 bit integer that specifies the length in octets of the RDATA
	 * field.
	 */
	char RDLENGTH;
	/**
	 * a variable length string of octets that describes the resource. The format of
	 * this information varies according to the TYPE and CLASS of the resource
	 * record. For example, the if the TYPE is A and the CLASS is IN, the RDATA
	 * field is a 4 octet ARPA Internet address.
	 */
	byte[] RDATA;

	@Override
	public List<Byte> toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		
		
		
		
		try {
			ParsingResult name = DomainName.parseDomainName(bytes, 0);
			int index = name.bytesUsed;
			this.nameString = name.string;
			
			// TODO: parse the TYPE, CLASS, TTL, and RDLength, which are fixed-length fields.
			
			
			
			// TODO: set the 'this.TYPE' using the bits of the RR.
			// TODO: do something like this, after the TYPE has been set.
			switch (this.TYPE) {
			case A: {
				this.nameString = null; // the IP address is in RDATA.
				break;
			}
			case CNAME: {
				this.nameString = DomainName.parseDomainName(this.RDATA).string;
				break;
			}
			case MX: {
				this.nameString = DomainName.parseDomainName(this.RDATA, 2).string;
				this.preference = ByteBuffer.wrap(this.RDATA).getShort();
				break;
			}
			case NS: {
				this.nameString = DomainName.parseDomainName(this.RDATA, 0).string;
				break;
			}
			default:
				break;
			}
		} catch (InvalidFormatException e) {
			System.err.println("Unable to parse the resourceRecord: " + e);
			System.exit(1);
		}
	}

	public String toConsoleString() throws InvalidFormatException {
		/**
		 * IP <tab> [ip address] <tab> [seconds can cache] <tab> [auth | nonauth] CNAME
		 * <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth] MX <tab>
		 * [alias] <tab> [pref] <tab> [seconds can cache] <tab> [auth | nonauth] NS
		 * <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth]
		 */
		assert this.RDLENGTH == this.RDATA.length;

		StringBuilder output = new StringBuilder();
		switch (this.TYPE) {
		case A: {
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
			return output.toString();
		}
		case CNAME: {
			output.append("CNAME\t");
			output.append(this.nameString);
			output.append('\t');
			output.append(this.TTL);
			output.append('\t');
			output.append("[TODO: Auth | no-auth]");
			return output.toString();
		}
		case MX: {
			output.append("MX\t");
			output.append(this.nameString);
			output.append('\t');
			short preference = ByteBuffer.wrap(this.RDATA).getShort();
			output.append(preference);
			output.append(this.TTL);
			output.append('\t');
			output.append("[TODO: Auth | no-auth]");
			return output.toString();
		}
		case NS: {
			output.append("NS\t");
			output.append(this.nameString);
			output.append('\t');
			output.append(this.TTL);
			output.append('\t');
			output.append("[TODO: Auth | no-auth]");
			return output.toString();
		}
		default:
			return "NOT SUPPORTED";
		}
	}

}
