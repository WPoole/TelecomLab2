package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.prefs.InvalidPreferencesFormatException;

import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;

import junit.framework.TestCase;

import org.junit.*;
import model.Message.Pointer;
import utils.Conversion;

class MessageTests extends TestCase {
	@Test
	void bytesAreCorrect() {
		Message test = new Message();
		
		short originalOffset = 52;
		String originalOffsetString = Conversion.binaryString(originalOffset);
		Pointer bob = test.new Pointer(originalOffset);
		
		byte[] bobBytes = bob.toBytes();
		String byteString = Conversion.binaryString(bobBytes[0]) + Conversion.binaryString(bobBytes[1]);
		
		assertEquals("11", byteString.substring(0,  2));
		assertEquals(originalOffsetString.substring(2, 16), byteString.substring(2, 16));
	}	

	@Test()
	void invalidBytesThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Message().new Pointer(
					(byte)0b0000_0000,
					(byte)0b0000_0000
			);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Message().new Pointer(
					(byte)0b1000_0000,
					(byte)0b0000_0000
			);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Message().new Pointer(
					(byte)0b0100_0000,
					(byte)0b0000_0000
			);
		});		
	}
	
	@Test()
	void rightOffsetIsRead() {
		Pointer bob = new Message().new Pointer(
				(byte)0b1100_0000,
				(byte)0b0000_1111 // the number 28.
		);
		assertEquals(15, (int)bob.offset);
	}
	
	
}
