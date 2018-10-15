package model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import model.enums.OpCode;
import model.enums.QClass;
import model.enums.ResponseCode;
import model.enums.Type;
import model.errors.InvalidFormatException;
import model.records.ResourceRecord;

public class Message implements BytesSerializable{
	public MessageHeader header;
	public Question[] questions;
	/**
	 * RRs answering the question
	 */
	public ResourceRecord[] answer;
	/**
	 * RRs pointing toward an authority
	 */
	public ResourceRecord[] authority;
	/**
	 * RRs holding additional information
	 */
	public ResourceRecord[] additional;
	
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
		

		this.questions = new Question[1];
		Question question = new Question();
		// TODO: Double-check this, but as far as I know, this behavior seems common to all three types (A, MX, NS).
		question.QNAME = name;
		question.QTYPE = type;
		question.QCLASS = QClass.IN;		
		this.questions[0] = question;
		
		this.answer = new ResourceRecord[0];
		this.authority = new ResourceRecord[0];
		this.additional = new ResourceRecord[0];
	}

	@Override
	public List<Byte> toBytes() {
		ArrayList<Byte> bytes = new ArrayList<>();
		bytes.addAll(this.header.toBytes());
		for(Question q : this.questions) {
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
		ByteBuffer buffer = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
		
		// create the Message instance.
		Message m = new Message();

		// First 12 bytes are the header bytes.
		byte[] headerBytes = new byte[12];
		buffer.get(headerBytes);
		
		m.header = MessageHeader.fromBytes(headerBytes);
		
		int index = 12;
		
		int questionCount = m.header.QDCOUNT;
		m.questions = new Question[questionCount];
		for(int i=0; i<questionCount; i++) {
			// TODO: parse the domain names, while also keeping track of how many bytes were read.
			Question newQuestion = Question.fromBytes(bytes, index);
			index += newQuestion.length();
		}
		
		m.answer = new ResourceRecord[m.header.ANCOUNT];
		for(int i=0; i<m.header.ANCOUNT; i++) {
			ResourceRecord rr = ResourceRecord.fromBytes(bytes, index, m.header.AA);
			index += rr.length();
			m.answer[i] = rr;
		}
		
		m.authority = new ResourceRecord[m.header.NSCOUNT];
		for(int i=0; i<m.header.NSCOUNT; i++) {
			ResourceRecord rr = ResourceRecord.fromBytes(bytes, index, m.header.AA);
			index += rr.length();
			m.authority[i] = rr;
		}
		
		m.additional = new ResourceRecord[m.header.ARCOUNT];
		for(int i=0; i<m.header.ARCOUNT; i++) {
			ResourceRecord rr = ResourceRecord.fromBytes(bytes, index, m.header.AA);
			index += rr.length();
			m.additional[i] = rr;
		}
		
		return m;
	}
	
	public boolean isErrorResponse() {
		return this.header.RCODE != ResponseCode.NO_ERROR;
	}
	
	public String getHeaderResponseCodeDescription() {
		switch(this.header.RCODE) {
			case FORMAT_ERROR:
				return this.header.RCODE.name() + ": The name server was unable to interpret the query.";
			case NAME_ERROR:
				return this.header.RCODE.name() + ": The domain name referenced in the query does not exist.";
			case SERVER_FAILURE:
				return this.header.RCODE.name() + ": The name server was unable to process this query due to a problem with the name server.";
			case REFUSED:
				return this.header.RCODE.name() + ": The name server refuses to perform the specified operation for policy reasons.";
			case NOT_IMPLEMENTED:
				return this.header.RCODE.name() + ": The name server does not support the requested kind of query.";
			default:
				return null;
		}
	}
	
}

