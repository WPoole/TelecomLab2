package utils;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import model.Pointer;
import model.errors.InvalidFormatException;

public class DomainNameTests{
	
	public static String testHostName = "www.facebook.com";
	public static void putHostName(ByteBuffer buffer, String hostname) {
		String[] words = hostname.split("\\.");
		for(String word : words) {
			buffer.put((byte) word.length());
			for(char c : word.toCharArray()) {
				buffer.put((byte) c);
			}
		}
	}
	
	public static int bytesNeeded(String hostname) {
		String[] words = hostname.split("\\.");
//		a.b.c.d --> 2 + 2 + 2 + 2 + 1
		int count = 0;
		for(String word : words) {
			count += 1; // the length bit
			count += word.length(); // one bit per character.
		}
		count++; // the empty byte at the end.		
		return count;
	}
	
	@Test
	public void testToBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(bytesNeeded(testHostName));
		putHostName(buffer, testHostName);
		buffer.put((byte) 0);
		byte[] expected = buffer.array();
		byte[] actual = DomainName.toBytes(testHostName);
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseSimpleLabelSequence() {		
		ByteBuffer buffer = ByteBuffer.allocate(bytesNeeded(testHostName));
		putHostName(buffer, testHostName);
		buffer.put((byte) 0);
		
		byte[] rawBytes = buffer.array();
		ParsingResult<String> actual;
		try {
			actual = DomainName.parseDomainName(rawBytes);
		} catch (InvalidFormatException e) {
			fail();
			return;
		}
		assertEquals(bytesNeeded(testHostName), actual.bytesUsed);
		assertEquals(testHostName, actual.result);
	}
	
	@Test
	public void testParseEmptyLabel() {
		byte[] rawBytes = new byte[] {0};
		ParsingResult<String> name;
		try {
			name = DomainName.parseDomainName(rawBytes);
		} catch (InvalidFormatException e) {
			fail();
			return;
		}
		assertEquals(1, name.bytesUsed);
		assertEquals("", name.result);
	}
	
	@Test
	public void testParsePointer() {
		String expected = testHostName;
		ByteBuffer buffer = ByteBuffer.allocate(100);
		
		buffer.position(0);
		Pointer p = new Pointer((short) 50);
		buffer.put(p.toBytes());
		
		// put the "www.mcgill.ca" at position 50, and put a pointer to it at position 0.
		buffer.position(50);
		putHostName(buffer, expected);
		buffer.put((byte) 0);
		
		byte[] rawBytes = buffer.array();
		ParsingResult<String> domainName;
		try {
			domainName = DomainName.parseDomainName(rawBytes, 0);
		} catch (InvalidFormatException e) {
			fail();
			return;
		}
		// only 2 bytes were read at position 0.
		assertEquals(2, domainName.bytesUsed);
		assertEquals(expected, domainName.result);
	}
	
	@Test
	public void testParseLabelSequenceEndingWithPointer() {
		String expected = "www.mcgill.ca";
		ByteBuffer buffer = ByteBuffer.allocate(100);
		
		// put the "www.mcgill" at position 0, followed by a Pointer to the ".ca" at position 50.
		buffer.position(3);
		putHostName(buffer, "www");
//		buffer.put((byte) 3).put((byte) 'w').put((byte) 'w').put((byte) 'w');
		Pointer p = new Pointer((short) 50);
		buffer.put(p.toBytes());
		
		buffer.position(50);
		buffer.put((byte) 6).put((byte) 'm').put((byte)'c').put((byte) 'g').put((byte)'i').put((byte)'l').put((byte)'l');
		buffer.put((byte) 2).put((byte) 'c').put((byte)'a');
		buffer.put((byte) 0);
		
		byte[] rawBytes = buffer.array();
		ParsingResult<String> domainName;
		try {
			domainName = DomainName.parseDomainName(rawBytes, 3);
		} catch (InvalidFormatException e) {
			fail();
			return;
		}
		// 6 bytes used (4 for "www" + 2 for the pointer)
		assertEquals(6, domainName.bytesUsed);
		assertEquals(expected, domainName.result);
	}
}
