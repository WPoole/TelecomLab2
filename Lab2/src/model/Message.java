package model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.enums.OpCode;
import model.enums.QClass;
import model.enums.ResponseCode;
import model.enums.Type;
import model.errors.InvalidFormatException;
import model.records.ResourceRecord;

public class Message implements BytesSerializable {
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
	 * Private constructor, only used when creating a message from bytes using the static factory method.
	 */
	private Message() {

	}

	/**
	 * Creates a new Message object using the given parameters.
	 * 
	 * @note To create a new message using bytes, use the default constructor and
	 *       the .fromBytes() instance method.
	 * @param qType
	 * @param dnsServerIpAddress
	 * @param name
	 */
	public Message(Type type, byte[] dnsServerIpAddress, String name) {
		this.header = new MessageHeader();
		// we are asking only one question.
		this.header.QDCOUNT = 1;
		Question question = new Question(name, type);
		question.QNAME = name;
		question.QTYPE = type;
		question.QCLASS = QClass.IN;
		this.questions = new Question[] {question};

		this.answer = new ResourceRecord[0];
		this.authority = new ResourceRecord[0];
		this.additional = new ResourceRecord[0];
	}

	@Override
	public List<Byte> toBytes() {
		ArrayList<Byte> bytes = new ArrayList<>();
		bytes.addAll(this.header.toBytes());
		for (Question q : this.questions)
			bytes.addAll(q.toBytes());
		for (ResourceRecord rr : this.answer)
			bytes.addAll(rr.toBytes());
		for (ResourceRecord rr : this.authority)
			bytes.addAll(rr.toBytes());
		for (ResourceRecord rr : this.additional)
			bytes.addAll(rr.toBytes());
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
		for (int i = 0; i < questionCount; i++) {
			// TODO: parse the domain names, while also keeping track of how many bytes were
			// read.
			Question newQuestion = Question.fromBytes(bytes, index);
			index += newQuestion.length();
		}

		m.answer = new ResourceRecord[m.header.ANCOUNT];
		for (int i = 0; i < m.header.ANCOUNT; i++) {
			ResourceRecord rr = ResourceRecord.fromBytes(bytes, index, m.header.AA);
			index += rr.length();
			m.answer[i] = rr;
		}

		m.authority = new ResourceRecord[m.header.NSCOUNT];
		for (int i = 0; i < m.header.NSCOUNT; i++) {
			ResourceRecord rr = ResourceRecord.fromBytes(bytes, index, m.header.AA);
			index += rr.length();
			m.authority[i] = rr;
		}

		m.additional = new ResourceRecord[m.header.ARCOUNT];
		for (int i = 0; i < m.header.ARCOUNT; i++) {
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
		switch (this.header.RCODE) {
		case FORMAT_ERROR:
			return this.header.RCODE.name() + ": The name server was unable to interpret the query.";
		case NAME_ERROR:
			return this.header.RCODE.name() + ": The domain name referenced in the query does not exist.";
		case SERVER_FAILURE:
			return this.header.RCODE.name()
					+ ": The name server was unable to process this query due to a problem with the name server.";
		case REFUSED:
			return this.header.RCODE.name()
					+ ": The name server refuses to perform the specified operation for policy reasons.";
		case NOT_IMPLEMENTED:
			return this.header.RCODE.name() + ": The name server does not support the requested kind of query.";
		default:
			return null;
		}
	}

	public void printToConsole() {
		if (this.isErrorResponse())
			System.out.println("ERROR\t" + this.getHeaderResponseCodeDescription());
		if (this.answer.length > 0)
			System.out.println("***Answer Section (" + this.answer.length + " records)***");
		for (ResourceRecord rr : this.answer)
			rr.printToConsole();
		if (this.authority.length > 0)
			System.out.println("***Authoritative Section (" + this.authority.length + " records)***");
		for (ResourceRecord rr : this.authority)
			rr.printToConsole();
		if (this.additional.length > 0)
			System.out.println("***Additional Section (" + this.additional.length + " records)***");
		for (ResourceRecord rr : this.additional)
			rr.printToConsole();
	}

}
