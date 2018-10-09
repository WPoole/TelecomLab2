package utils;

import java.util.ArrayList;

import model.Pointer;

public class DomainName {
	public static byte[] toBytes(String domainName) throws Exception {
		if (!isValidDomainName(domainName)) {
			throw new Exception("domain name is not valid.");
		}
		String[] words = domainName.split(".");
		int bytesNeeded = domainName.length() + 2;
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

	private static boolean isValidDomainName(String domainName) {
		// todo;
		return false;
	}

	public String[] parseDomainNames(byte[] rawBytes) throws Exception {
		ArrayList<String> domainNames = new ArrayList<>();
		// three options:
		// a sequence of labels ending in a zero octet
		// a sequence of labels ending in a pointer
		// a pointer.
		// TODO: don't know what the "stop" condition is on the decoding! (setting it to
		// be the length of rawBytes for now.
		for (int index = 0; index < rawBytes.length; index++) {
			int bytesUsed = 0;
			if (isLabel(rawBytes[index])) {
				String domainName = parseLabelSequence(rawBytes, 0);
				/**
				 * The number of bytes used is: - one byte per character in the resulting
				 * string; - without counting each '.'; - plus one 'length' byte at the start of
				 * each word; (possibly also a null byte at the end.)
				 * 
				 * which effectively comes down to (total length) - (#words-1) + (#words) =
				 * totalLength + 1
				 */
				bytesUsed = domainName.length() + 1;

				if (isNullLabel(rawBytes[index])) {
					domainNames.add(domainName);
				} else if (isPointer(rawBytes[index])) {
					/**
					 * the sequence of labels ends in a pointer. Parse the label at the offset and
					 * add it to the end.
					 */
					Pointer endPointer = new Pointer(rawBytes[index], rawBytes[index + 1]);
					String addition = parseLabelSequence(rawBytes, endPointer.offset);
					domainName += addition;
					domainNames.add(domainName);

					// we used only two bytes for the pointer.
					bytesUsed = 2;

				} else {
					throw new Exception("Label sequence must be terminated by either a zero octet or a pointer.");
				}
			} else if (isPointer(rawBytes[index])) {
				Pointer pointer = new Pointer(rawBytes[index]);
				domainNames.add(parseLabelSequence(rawBytes, pointer.offset));
				bytesUsed = 2;
			} else {
				throw new Exception("Expected either a label or a pointer, got:"
						+ Conversion.binaryString(rawBytes[index]) + " at position " + index);
			}
			index += bytesUsed;
		}
		return (String[]) domainNames.toArray();
	}

	private boolean isPointer(byte b) {
		String firstByte = Conversion.binaryString(b);
		return firstByte.charAt(0) == '1' && firstByte.charAt(1) == '1';
	}

	private boolean isLabel(byte b) {
		String firstByte = Conversion.binaryString(b);
		return firstByte.charAt(0) == '0' && firstByte.charAt(1) == '0' && b != 0x00;
	}

	private boolean isNullLabel(byte b) {
		return b == 0;
	}

	public String parseLabelSequence(byte[] rawBytes, int startingOffset) {
		assert isLabel(rawBytes[startingOffset]);

		StringBuilder name = new StringBuilder();

		int index = startingOffset;

		while (isLabel(rawBytes[index])) {
			// start decoding a label, starting at position 'index', then move
			// the pointer to the start of the 'word'
			int length = rawBytes[index++];

			for (int i = 0; i < length; i++, index++) {
				name.append((char) rawBytes[i]);
			}
			// add the '.' between 'words'.
			if (isLabel(rawBytes[index])) {
				name.append('.');
			}
		}
		return name.toString();
	}

}
