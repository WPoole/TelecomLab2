package utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ObjectOutputStream.PutField;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import junit.framework.TestCase;
import model.errors.InvalidFormatException;

class DomainNameTests extends TestCase {
	@Test
	void testToBytes() {
		String domainName = "www.mcgill.ca";
		byte[] expected = ByteBuffer.allocate(4 + 7 + 3 + 1)
				.put((byte) 3).put((byte) 'w').put((byte) 'w').put((byte) 'w')
				.put((byte)6).put((byte) 'm').put((byte)'c').put((byte) 'g').put((byte)'i').put((byte)'l').put((byte)'l')
				.put((byte)2).put((byte) 'c').put((byte) 'a')
				.put((byte)0)
				.array();
		byte[] actual = DomainName.toBytes(domainName);
		assertArrayEquals(expected, actual);
	}

	@Test
	void testParseSimpleLabelSequence() {
		String expected = "www.mcgill.ca";
		byte[] rawBytes = ByteBuffer.allocate(4 + 7 + 3 + 1)
				.put((byte) 3).put((byte) 'w').put((byte) 'w').put((byte) 'w')
				.put((byte)6).put((byte) 'm').put((byte)'c').put((byte) 'g').put((byte)'i').put((byte)'l').put((byte)'l')
				.put((byte)2).put((byte) 'c').put((byte) 'a')
				.put((byte)0)
				.array();
		ParsingResult<String> actual;
		try {
			actual = DomainName.parseDomainName(rawBytes);
		} catch (InvalidFormatException e) {
			fail();
			return;
		}
		assertEquals(15, actual.bytesUsed);
		assertEquals(expected, actual.result);
	}

}
