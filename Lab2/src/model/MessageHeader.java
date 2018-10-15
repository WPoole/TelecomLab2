package model;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import model.enums.OpCode;
import model.enums.ResponseCode;
import model.errors.InvalidFormatException;
import utils.Conversion;


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
public class MessageHeader implements BytesSerializable{
	
//	public static short defaultId = 123;
	
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
	int Z = 0;
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

	public MessageHeader() {
		
	}
	
	@Override
	public List<Byte> toBytes() {
		String flags = 
				Conversion.binaryString(this.QR) +
				Conversion.binaryString(this.OPCODE.value, 4) +
				Conversion.binaryString(this.AA) +
				Conversion.binaryString(this.TC) +
				Conversion.binaryString(this.RD) +
				Conversion.binaryString(this.RA) +
				"000" + // Z value must be zero, (reserved for future use).
				Conversion.binaryString(this.RCODE.value, 4);
		
		
		
		ByteBuffer buffer = ByteBuffer.allocate(6 * 2)
				.putShort(this.ID)
				.putShort(Short.parseShort(flags, 2))
				.putShort(this.QDCOUNT)
				.putShort(this.ANCOUNT)
				.putShort(this.NSCOUNT)
				.putShort(this.ARCOUNT);
		byte[] bytes = buffer.array();
		
		ArrayList<Byte> result = new ArrayList<Byte>();
		for(byte b : bytes){
			result.add(b);
		}
		return result;
	}

	
	public static MessageHeader fromBytes(byte[] bytes){
		MessageHeader header = new MessageHeader();
		try {
			ShortBuffer buffer = ByteBuffer.wrap(bytes).asShortBuffer().asReadOnlyBuffer();
			header.ID = buffer.get();
			String flags = Conversion.binaryString(buffer.get());
			header.QR = flags.charAt(0) == '1';
			header.OPCODE = OpCode.fromString(flags.substring(1, 5));
			header.AA = flags.charAt(5) == '1';
			header.TC = flags.charAt(6) == '1';
			header.RD = flags.charAt(7) == '1';
			header.RA = flags.charAt(8) == '1';
			header.Z = 0;
			header.RCODE = ResponseCode.fromString(flags.substring(12,16));
			header.QDCOUNT = buffer.get();
			header.ANCOUNT = buffer.get();
			header.NSCOUNT = buffer.get();
			header.ARCOUNT = buffer.get();
		}catch(InvalidFormatException e) {
			System.err.println("ERROR: Unable to parse the message header");
			e.printStackTrace();
		}
		return header;
		
	}
}
