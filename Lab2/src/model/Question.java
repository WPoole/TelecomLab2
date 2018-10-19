package model;

import java.nio.ByteBuffer;

import model.enums.QClass;
import model.enums.Type;
import model.errors.InvalidFormatException;
import utils.DomainName;
import utils.ParsingResult;

public class Question implements BytesSerializable {
	/**
	 * a domain name represented as a sequence of labels, where each label consists
	 * of a length octet followed by that number of octets. The domain name
	 * terminates with the zero length octet for the null label of the root. Note
	 * that this field may be an odd number of octets; no padding is used.
	 */
	String QNAME;
	private byte[] qNameBytes;
	/**
	 * a two octet code which specifies the type of the query. The values for this
	 * field include all codes valid for a TYPE field, together with some more
	 * general codes which can match more than one type of RR.
	 */
	Type QTYPE;
	/**
	 * a two octet code that specifies the class of the query. For example, the
	 * QCLASS field is IN for the Internet.
	 */
	QClass QCLASS;

	public Question(String name, Type type) {
		this.QNAME = name;
		this.QTYPE = type;
		this.QCLASS = QClass.IN;
	}
	
	/**
	 * Constructor used only in the static factory method.
	 */
	private Question() {}
	
	
	@Override
	public byte[] toByteArray() {
		if (this.qNameBytes == null) {
			// if we haven't decoded the domain name string yet, do so now.
			this.qNameBytes = DomainName.toBytes(this.QNAME);
		}
		byte[] bytes = ByteBuffer.allocate(this.length())
				.put(this.qNameBytes)
				.put(this.QTYPE.toBytes())
				.put(this.QCLASS.toBytes())
				.array();
		return bytes;
	}

	/**
	 * Returns The number of bytes of the message which correspond to this
	 * QuestionEntry.
	 * 
	 * @return
	 */
	public int length() {
		if (this.qNameBytes == null) {
			// if we haven't decoded the domain name string yet, do so now.
			this.qNameBytes = DomainName.toBytes(this.QNAME);
		}
		return this.qNameBytes.length + 4;
	}

	public static Question fromBytes(byte[] rawBytes, int offset) throws InvalidFormatException {
		Question question = new Question();
		ByteBuffer buffer = ByteBuffer.wrap(rawBytes, offset, rawBytes.length-offset).asReadOnlyBuffer();
		ParsingResult<String> domainName = DomainName.parseDomainName(rawBytes, offset);
		
		question.qNameBytes = new byte[domainName.bytesUsed];
		buffer.get(question.qNameBytes);

		question.QNAME = domainName.result;
		question.QTYPE = Type.fromBytes(buffer.get(), buffer.get());
		question.QCLASS = QClass.fromBytes(buffer.get(), buffer.get());
		
		return question;
	}
}