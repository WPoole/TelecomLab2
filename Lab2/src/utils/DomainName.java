package utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import model.Pointer;
import model.errors.InvalidFormatException;

public class DomainName {

	/**
	 * Converts a domain name string ("www.mcgill.ca", for example) to sequence of
	 * labels ending in an empty octet.
	 * 
	 * @param domainName the domain name.
	 * @return the array of bytes which represent the sequence of labels.
	 */
	public static byte[] toBytes(String domainName) {
		if (!isValidDomainName(domainName)) {
			throw new IllegalArgumentException("domain name '" + domainName + "' is not valid.");
		}
		/**
		 * we need one byte per word (for the length), plus one byte per character
		 * (which isn't a '.') plus one byte for the zero-length at the end. (This is
		 * also equivalent to the length of the original string + 2).
		 */
		String[] words = domainName.split("\\.");
		int numberOfChars = domainName.replace(".", "").length();
		int bytesNeeded = words.length + numberOfChars + 1;

		byte[] result = new byte[bytesNeeded];
		int i = 0;
		for (String word : words) {
			result[i++] = (byte) word.length();
			for (char c : word.toCharArray()) {
				// TODO: get ASCII value for the given char.
				result[i++] = (byte) c;
			}
		}
		// add the final zero octet.
		result[i] = 0;
		return result;
	}

	/**
	 * Determines if the given domainName is valid.
	 * 
	 * @param domainName a domain name (ex: "www.mcgill.ca")
	 * @return
	 */
	public static boolean isValidDomainName(String domainName) {
		if (domainName == null)
			return false;
		if (domainName.equals(""))
			return false;
		if (domainName.contains(" "))
			return false;
		if (!domainName.contains("."))
			return false;
		if (domainName.contains(".."))
			return false;
		String[] words = domainName.split(".");
		for (String word : words) {
			if (word.length() > 63)
				return false;
			if (!isASCII(word))
				return false;
		}
		return true;
	}

	private static boolean isPointer(byte b) {
		String firstByte = Conversion.binaryString(b);
		return firstByte.charAt(0) == '1' && firstByte.charAt(1) == '1';
	}

	private static boolean isLabel(byte b) {
		String firstByte = Conversion.binaryString(b);
		return firstByte.charAt(0) == '0' && firstByte.charAt(1) == '0' && !isNullLabel(b);
	}

	private static boolean isNullLabel(byte b) {
		return b == 0;
	}
	
	public static ParsingResult<String> parseDomainName(byte[] rawBytes) throws InvalidFormatException{
		return parseDomainName(rawBytes, 0);
	}
	
	/**
	 * Decode a (possibly compressed) sequence of labels. Returns both the resulting String and the number of bytes
	 * used to produce it.
	 * 
	 * @param rawBytes
	 * @param startingOffset
	 * @return
	 * @throws InvalidFormatException
	 */
	public static ParsingResult<String> parseDomainName(byte[] rawBytes, int startingOffset) throws InvalidFormatException{
		assert isLabel(rawBytes[startingOffset]);

		StringBuilder domainName = new StringBuilder();
		int index = startingOffset;
		int bytesUsed = 0;
		int wordCount = 0;
		// while the current position is a (non-empty) label
		while (isLabel(rawBytes[index])) {
			if (wordCount != 0) {
				// add the '.' between words
				domainName.append(".");
			}
			// parse a single label (a word of the hostname).
			ParsingResult<String> wordResult = parseLabel(rawBytes, index);
			domainName.append(wordResult.result);
			bytesUsed += wordResult.bytesUsed;
			wordCount++;
			index = startingOffset + bytesUsed;
		}
		if (isNullLabel(rawBytes[index])) {
			bytesUsed += 1;
		} else if (isPointer(rawBytes[index])) {
			// TODO: comment/uncomment the following to try to read the compressed domain name.
			// throw new InvalidFormatException("Pointers aren't handled yet!");
			
			// create the pointer
			Pointer p = new Pointer(rawBytes[index], rawBytes[index+1]);
			// decode the domain name at the pointer's offset ( recursive)
			ParsingResult result = parseDomainName(rawBytes, p.offset);
			domainName.append(result.result);
			// NOTE: we used only two bytes! (since it was a pointer)
			bytesUsed += 2;
		}

		return new ParsingResult(domainName.toString(), bytesUsed);
	}

	private static ParsingResult parseLabel(byte[] rawBytes, int offset) throws InvalidFormatException {
		assert isLabel(rawBytes[offset]);
		// read the 'length' byte, and move the pointer to the next byte.
		int length = rawBytes[offset++];
		StringBuilder word = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = (char) rawBytes[offset + i];
			if (isASCII(c)) {
				word.append(c);
			} else {
				throw new InvalidFormatException("Non-ascii character encountered in a label.");
			}
		}
		// we used one byte per char, plus the length byte at the start.
		String result = word.toString();
		int bytesUsed = 1 + result.length();
		return new ParsingResult(result, bytesUsed);
	}

	private static boolean isASCII(char c) {
		return c <= 0x7F;
	}

	private static boolean isASCII(String s) {
		for (char c : s.toCharArray()) {
			if (!isASCII(c))
				return false;
		}
		return true;
	}

}
