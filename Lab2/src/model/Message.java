package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utils.Conversion;

//+---------------------+
//|        Header       |
//+---------------------+
//|       Question      | the question for the name server
//+---------------------+
//|        Answer       | RRs answering the question
//+---------------------+
//|      Authority      | RRs pointing toward an authority
//+---------------------+
//|      Additional     | RRs holding additional information
//+---------------------+

public class Message implements BytesSerializable{
	Header header;
	QuestionEntry[] question;
	/**
	 * RRs answering the question
	 */
	ResourceRecord[] answer;
	/**
	 * RRs pointing toward an authority
	 */
	ResourceRecord[] authority;
	/**
	 * RRs holding additional information
	 */
	ResourceRecord[] additional;
	
	public Message(){
		// TODO
	}
	
	private String[] parseDomainNames(byte[] rawBytes) throws Exception {
		int i=0;
		ArrayList<String> domainNames = new ArrayList<>();
		
		//three options: 
		// - a pointer	
		String firstByte = Conversion.binaryString(rawBytes[0]);
		if(firstByte.charAt(i) == '1' && firstByte.charAt(i+1) == '1') {
			Pointer pointer = new Pointer(rawBytes[i], rawBytes[i+1]);
			domainNames.add(parseLabelSequence(rawBytes, pointer.offset));
		}
		// - a label
		// The first two bits can only be "00", since we are dealing with a label.
		if (!(firstByte.charAt(i) == '0' && firstByte.charAt(i+1) == '0')) {
			throw new Exception("The first two bits of a label should be '00'");
		}
		// parse the sequence of labels, and stop if we reach either a zero octet or a pointer.
		String label = parseLabelSequence(rawBytes, i);
		
		// TODO:
		
		
		
		return null;
	}
	
	private boolean isPointer(byte b) {
		String firstByte = Conversion.binaryString(b);
		return firstByte.charAt(0) == '1' && firstByte.charAt(1) == '1';
	}
	
	private boolean isLabel(byte b) {
		String firstByte = Conversion.binaryString(b);
		return firstByte.charAt(0) == '0' && firstByte.charAt(1) == '0';
	}
	
	
	private String parseLabelSequence(byte[] rawBytes, int startingOffset) {
		return null;
	}
	
	
	
	public class Pointer{
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
	
	
	
	/**This is the format of the message header:
	 *  					           1  1  1  1  1  1  
	 *  0   1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                     ID                        |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |QR|  Opcode   |AA|TC|RD|RA|  Z     |   RCODE   |      
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   QDCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   ANCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   NSCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   ARCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 */
	class Header implements BytesSerializable{
		/**
		 * A 16 bit identifier assigned by the program that generates any kind
		 * of query. This identifier is copied the corresponding reply and can
		 * be used by the requester to match up replies to outstanding queries.
		 */
		int ID;
		/**
		 * A one bit field that specifies whether this message is a query (0),
		 * or a response (1).
		 */
		boolean QR;
		/**
		 * A four bit field that specifies kind of query in this message. This
		 * value is set by the originator of a query and copied into the
		 * response. The values are:
		 */
		OpCode OPCODE;
		/**
		 * Authoritative Answer - this bit is valid in responses, and specifies
		 * that the responding name server is an authority for the domain name
		 * in question section.
		 */
		boolean AA;
		/**
		 * TrunCation - specifies that this message was truncated due to length
		 * greater than that permitted on the transmission channel.
		 */
		boolean TC;
		/**
		 * Recursion Desired - this bit may be set in a query and is copied into
		 * the response. If RD is set, it directs the name server to pursue the
		 * query recursively. Recursive query support is optional.
		 */
		boolean RD;
		/**
		 * Recursion Available - this be is set or cleared in a response, and
		 * denotes whether recursive query support is available in the name
		 * server.
		 */
		boolean RA;
		/**
		 * Reserved for future use. Must be zero in all queries and responses.
		 */
		int Z;
		/**
		 * Response code - this 4 bit field is set as part of responses.
		 */
		ResponseCode RCODE;
		/**
		 * an unsigned 16 bit integer specifying the number of entries in the
		 * question section.
		 */
		short QDCOUNT;
		/**
		 * an unsigned 16 bit integer specifying the number of resource records
		 * in the answer section.
		 */
		short ANCOUNT;
		/**
		 * an unsigned 16 bit integer specifying the number of name server
		 * resource records in the authority records section.
		 */
		short NSCOUNT;
		/**
		 * an unsigned 16 bit integer specifying the number of resource records
		 * in the additional records section
		 */
		short ARCOUNT;

		Header() {
			// TODO
		}

		@Override
		public List<Byte> toBytes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fromBytes(byte[] bytes) {
			// TODO Auto-generated method stub
			
		}
	}

	/** Enum for the Message Header's OPCODE field. */
	enum OpCode {
		/** a standard query */
		QUERY,
		/** an inverse query */
		IQUERY,
		/** a server status request */
		STATUS, RESERVED;
	}

	enum ResponseCode {
		/** No error condition */
		NO_ERROR,
		/**
		 * The name server was unable to interpret the query
		 */
		FORMAT_ERROR,
		/**
		 * The name server was unable to process this query due to a problem
		 * with the name server.
		 */
		SERVER_FAILURE,
		/**
		 * Meaningful only for responses from an authoritative name server, this
		 * code signifies that the domain name referenced in the query does not
		 * exist.
		 */
		NAME_ERROR,
		/**
		 * The name server does not support the requested kind of query.
		 */
		NOT_IMPLEMENTED,
		/**
		 * The name server refuses to perform the specified operation for policy
		 * reasons. For example, a name server may not wish to provide the
		 * information to the particular requester, or a name server may not
		 * wish to perform a particular operation (e.g., zone transfer) for
		 * particular data.
		 */
		REFUSED,
	}

	class QuestionEntry implements BytesSerializable {
		/**
		 * a domain name represented as a sequence of labels, where each label
		 * consists of a length octet followed by that number of octets. The
		 * domain name terminates with the zero length octet for the null label
		 * of the root. Note that this field may be an odd number of octets; no
		 * padding is used.
		 */
		byte[] QNAME;
		/**
		 * a two octet code which specifies the type of the query. The values
		 * for this field include all codes valid for a TYPE field, together
		 * with some more general codes which can match more than one type of
		 * RR.
		 */
		QueryType QTYPE;
		/**
		 * a two octet code that specifies the class of the query. For example,
		 * the QCLASS field is IN for the Internet.
		 */
		QueryClass qClass;
		@Override
		public List<Byte> toBytes() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void fromBytes(byte[] bytes) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public enum QueryType {
		/** A request for a transfer of an entire zone */
		AXFR (252),
		/** A request for mailbox-related records (MB, MG or MR) */
		MAILB (253),
		/** A request for mail agent RRs (Obsolete - see MX) */
		MAILA (254),
		/** ('*') 255 A request for all records */
		ALL (255);
		public final int value;
		private QueryType(int value){
			this.value = value;
		}
	}
	enum QueryClass {
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
		private QueryClass(int value){
			this.value = value;
		}
	}
	@Override
	public ArrayList<Byte> toBytes() {
		// TODO Auto-generated method stub
		ArrayList<Byte> bytes = new ArrayList<>();
		bytes.addAll(this.header.toBytes());
		for(QuestionEntry q : this.question) {
			bytes.addAll(q.toBytes());
		}
		for(ResourceRecord rr : this.answer) {
			bytes.addAll(rr.toBytes());
		}
		for(ResourceRecord rr : this.authority) {
			bytes.addAll(rr.toBytes());
		}
		for(ResourceRecord rr : this.additional) {
			bytes.addAll(rr.toBytes());
		}
		return bytes;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
	}
}

