package model;

class ResourceRecord {
	/**
	 * An owner name, i.e., the name of the node to which this resource record
	 * pertains. ("a domain name is terminated by a length byte of zero. The
	 * high order two bits of every length octet must be zero, and the remaining
	 * six bits of the length field limit the label to 63 octets or less."
	 */
	byte[] NAME;
	/**
	 * two octets containing one of the RR type codes. This field specifies the
	 * meaning of the data in the RDATA field.
	 */
	RRType TYPE;
	/**
	 * two octets which specify the class of the data in the RDATA field.
	 */
	RRClass CLASS;
	/**
	 * a 32 bit unsigned integer that specifies the time interval (in seconds)
	 * that the resource record may be cached before it should be discarded.
	 * Zero values are interpreted to mean that the RR can only be used for the
	 * transaction in progress, and should not be cached.
	 */
	int TTL;
	/**
	 * an unsigned 16 bit integer that specifies the length in octets of the
	 * RDATA field.
	 */
	char RDLENGTH;
	/**
	 * a variable length string of octets that describes the resource. The
	 * format of this information varies according to the TYPE and CLASS of the
	 * resource record. For example, the if the TYPE is A and the CLASS is IN,
	 * the RDATA field is a 4 octet ARPA Internet address.
	 */
	byte[] RDATA;

	enum RRClass {

	}

	public enum RRType {
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

		private RRType(int value) {
			this.value = value;
		}
	}

}
