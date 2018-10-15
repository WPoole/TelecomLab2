package model;

import static org.junit.Assert.*;

import org.junit.Test;
import utils.Conversion;

public class MessageTests {
	
	
	
	@Test
	public void bytesAreCorrect() {
		
		short originalOffset = 52;
		String originalOffsetString = Conversion.binaryString(originalOffset);
		Pointer bob = new Pointer(originalOffset);
		
		byte[] bobBytes = bob.toBytes();
		String byteString = Conversion.binaryString(bobBytes[0]) + Conversion.binaryString(bobBytes[1]);
		
		assertEquals("11", byteString.substring(0,  2));
		assertEquals(originalOffsetString.substring(2, 16), byteString.substring(2, 16));
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidBytesThrowsException1() {
		new Pointer(
				(byte)0b0000_0000,
				(byte)0b0000_0000
		);
	}
	@Test(expected = IllegalArgumentException.class)
	public void invalidBytesThrowsException2() {
		new Pointer(
				(byte)0b1000_0000,
				(byte)0b0000_0000
		);	
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidBytesThrowsException3() {
		new Pointer(
				(byte)0b0100_0000,
				(byte)0b0000_0000
		);
	}

	@Test()
	public void rightOffsetIsRead() {
		Pointer bob = new Pointer(
				(byte)0b1100_0000,
				(byte)0b0000_1111 // the number 28.
		);
		assertEquals(15, (int)bob.offset);
	}
	
	
}
