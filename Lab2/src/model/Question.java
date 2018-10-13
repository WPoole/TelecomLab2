package model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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

	public static Question fromBytes(byte[] rawBytes) throws InvalidFormatException {
		Question question = new Question();
		ByteBuffer buffer = ByteBuffer.wrap(rawBytes).asReadOnlyBuffer();
		ParsingResult<String> result = DomainName.parseDomainName(rawBytes);
		question.QNAME = result.result;
		
		question.qNameBytes = new byte[result.bytesUsed];
		buffer.get(question.qNameBytes);
		question.QTYPE = Type.fromBytes(buffer.get(), buffer.get());
		question.QCLASS = QClass.fromBytes(buffer.get(), buffer.get());
		
		return question;
	}
}