package model;



public class ResourceRecord {
	public enum RRClass {
		bobo;
	}
	public enum RRType {
		/** a host address */
		A	(1),
		/** an authoritative name server */
		NS	(2),
		/** a mail destination (Obsolete - use MX)*/
		MD 	(3),
		/** a mail forwarder (Obsolete - use MX)*/
		MF	(4),
		/** the canonical name for an alias*/
		CNAME 	(5),
		/** marks the start of a zone of authority*/
		SOA (6),
		/** a mailbox domain name (EXPERIMENTAL)*/
		MB (7),
		/** a mail group member (EXPERIMENTAL)*/
		MG	(8),
		/** a mail rename domain name (EXPERIMENTAL)*/
		MR	(9),
		/** a null RR (EXPERIMENTAL)*/
		NULL	(10),
		/** a well known service description*/
		WKS	(11),
		/** a domain name pointer*/
		PTR	(12),
		/** host information*/
		HINFO	(13),
		/** mailbox or mail list information*/
		MINFO	(14),
		/** mail exchange*/
		MX	(15),
		/** text strings*/
		TXT	(16);
		
		public final int value;
		private RRType(int value){
			this.value = value;
		}
	}
	
	public enum QTypes {
		/** A request for a transfer of an entire zone */
		AXFR (252),
		/** A request for mailbox-related records (MB, MG or MR) */
		MAILB (253),
		/** A request for mail agent RRs (Obsolete - see MX) */
		MAILA (254),
		/** ('*') 255 A request for all records */
		STAR (255);
		public final int value;
		private QTypes(int value){
			this.value = value;
		}
	}
	
	/**
	 * An owner name, i.e., the name of the node to which this resource record pertains.
	 * ("a domain name is terminated by a length byte of zero. The high order two bits 
	 * of every length octet must be zero, and the remaining six bits of the length field
	 * limit the label to 63 octets or less."
	 */
	public byte[] NAME;
	public RRType TYPE;
	public RRClass CLASS;
	public int TTL;
	public char RDLENGTH;
	/**
	 * a variable length string of octets that describes the resource.
	 * The format of this information varies according to the TYPE and CLASS of the resource record.
	 */
	public byte[] RDATA;
	
}
