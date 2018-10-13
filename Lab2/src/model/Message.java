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
import model.records.ResourceRecord;
import utils.Conversion;
import utils.DomainName;
import utils.ParsingResult;
import model.records.ResourceRecord;
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
	MessageHeader header;
	Question[] question;
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
	
	/**
	 * Default constructor, used mainly when creating a message from bytes (ex: new Message().fromBytes)
	 */
	public Message() {
		
	}
	
	/**
	 * Creates a new Message object using the given parameters.
	 * @note To create a new message using bytes, use the default constructor and the .fromBytes() instance method.
	 * @param qType
	 * @param dnsServerIpAddress
	 * @param name
	 */
	public Message(Type type, byte[] dnsServerIpAddress, String name) {
		// TODO: populate all the fields correctly.
		
		this.header = new MessageHeader();
		this.header.ID = MessageHeader.defaultId; // some integer we always use.
		this.header.QR = false; // sending a query.
		this.header.OPCODE = OpCode.QUERY; // we are sending a standard Query.
		this.header.AA = false; // this is not an authoritative answer.
		this.header.TC = false; // we are (hopefully) not going to send a truncated message.
		this.header.RD = false; // (not sure) recursion desired. optional.
		this.header.Z = 0; // (always zero).
		this.header.RCODE = ResponseCode.NO_ERROR; // will be set by the server during the response.
		this.header.QDCOUNT = 1; // we are asking one question (one domain name)
		this.header.ANCOUNT = 0; // no resource records in answer (this is a question)
		this.header.NSCOUNT = 0; // same here
		this.header.ARCOUNT = 0; // same here also.
		

		Question question = new Question();
		// TODO: Double-check this, but as far as I know, this behavior seems common to all three types (A, MX, NS).
		question.QNAME = name;
		question.QTYPE = type;
		question.QCLASS = QClass.IN;
		switch(type) {
			case A:{						
			}
			case MX:{
			}
			case NS: {
			}
			default:
				break;
		}	
	}

	@Override
	public List<Byte> toBytes() {
		// TODO Auto-generated method stub
		ArrayList<Byte> bytes = new ArrayList<>();
		bytes.addAll(this.header.toBytes());
		for(Question q : this.question) {
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
	
	
	public static Message fromBytes(byte[] bytes) throws InvalidFormatException {
		Message m = new Message();
		ByteBuffer buffer = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
		
		// bytes 0 to 12 (exclusive) are the header bytes.
		byte[] headerBytes = new byte[12];
		buffer.get(headerBytes);
		m.header = MessageHeader.fromBytes(headerBytes);
		
		if(m.header.RCODE != ResponseCode.NO_ERROR) {
			switch(m.header.RCODE) {
				case FORMAT_ERROR:
					System.err.println("ERROR \t The name server was unable to interpret the query.");
				case NAME_ERROR:
					System.err.println("ERROR \t The domain name referenced in the query does not exist.");
				case SERVER_FAILURE:
					System.err.println("ERROR \t The name server was unable to process this query due to a problem with the name server.");
				case REFUSED:
					System.err.println("ERROR \t The name server refuses to perform the specified operation for policy reasons.");
				case NOT_IMPLEMENTED:
					System.err.println("ERROR \t The name server does not support the requested kind of query.");
				default:
					break;
			}
			System.exit(1);
		}
				
		int index = 12; 
		byte[] bytesLeft = Arrays.copyOfRange(bytes,  index, bytes.length);
		
		
		int questionCount = m.header.QDCOUNT;
		m.question = new Question[questionCount];
		for(int i=0; i<questionCount; i++) {
			// TODO: parse the domain names, while also keeping track of how many bytes were read.
			Question newQuestion = Question.fromBytes(bytesLeft);
			index += newQuestion.length();
		}
		// TODO: implement the rest.
		
		return m;
		
	}
}

