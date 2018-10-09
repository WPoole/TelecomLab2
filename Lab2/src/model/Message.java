package model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.platform.commons.util.StringUtils;

import model.enums.*;
import model.errors.InvalidFormatException;
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
	public class Header implements BytesSerializable{
		/**
		 * A 16 bit identifier assigned by the program that generates any kind
		 * of query. This identifier is copied the corresponding reply and can
		 * be used by the requester to match up replies to outstanding queries.
		 */
		short ID;
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
			
			String flags = 
					Conversion.binaryString(this.QR) +
					Conversion.binaryString(this.OPCODE) +
					Conversion.binaryString(this.AA) +
					Conversion.binaryString(this.TC) +
					Conversion.binaryString(this.RD) +
					Conversion.binaryString(this.RA) +
					"000" + // Z value must be zero, (reserved for future use).
					Conversion.binaryString(this.RCODE);
			
			ByteBuffer buffer = ByteBuffer.allocate(6 * 2)
					.putShort(this.ID)
					.putShort(Short.parseShort(flags, 2))
					.putShort(this.QDCOUNT)
					.putShort(this.ANCOUNT)
					.putShort(this.NSCOUNT)
					.putShort(this.ARCOUNT);
			byte[] bytes = buffer.array();
			return Arrays.asList();			
		}

		@Override
		public void fromBytes(byte[] bytes){
			try {
				ShortBuffer buffer = ByteBuffer.allocate(6 * 2).put(bytes)
						.asShortBuffer().asReadOnlyBuffer();
				this.ID = buffer.get();
				String flags = Conversion.binaryString(buffer.get());
				this.QR = flags.charAt(0) == '1';
				this.OPCODE = OpCode.fromString(flags.substring(1, 5));
				this.AA = flags.charAt(5) == '1';
				this.TC = flags.charAt(6) == '1';
				this.RD = flags.charAt(7) == '1';
				this.RA = flags.charAt(8) == '1';
				this.Z = 0;
				this.RCODE = ResponseCode.fromString(flags.substring(12,16));
				this.QDCOUNT = buffer.get();
				this.ANCOUNT = buffer.get();
				this.NSCOUNT = buffer.get();
				this.ARCOUNT = buffer.get();
			}catch(InvalidFormatException e) {
				
			}
		}
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
		
	@Override
	public List<Byte> toBytes() {
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

