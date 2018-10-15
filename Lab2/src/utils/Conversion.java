package utils;

import java.util.ArrayList;

public class Conversion {
	public static String binaryString(byte value) {
		return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
	}
	public static String binaryString(short value) {
		return String.format("%16s", Integer.toBinaryString(value & 0xFFFF)).replace(' ', '0');
	}
	public static String binaryString(int value) {
		return Integer.toBinaryString(value);
	}
	public static String binaryString(boolean value) {
		return value ? "1" : "0";
	}
	public static String binaryString(int value, int width) {
		return binaryString(value, width, '0');
	}
	/**
	 * Returns the binary representation of the given value as a binary string of length "width".
	 * The "paddingChar" character is used for the left-padding.
	 * @param value the value to convert.
	 * @param width the width of the resulting binary string.
	 * @param paddingChar the character to use for padding. defaults to '0'.
	 * @return the binary representation of the value.
	 */
	public static String binaryString(int value, int width, char paddingChar) {
		return String.format("%"+width+"s", Integer.toBinaryString(value)).replace(' ', paddingChar);
	}
	
	public static byte[] ipAddressStringToByteArray(String ipAddressString) {
		String[] serverIpComponents = ipAddressString.split("\\.");
		byte[] serverIpAddressInBytes = new byte[serverIpComponents.length];
		for(int i = 0; i < serverIpComponents.length; i++) {
			// Note: The below line should not throw any exceptions since we already did all necessary
			// checks when forming the InputData object.
			serverIpAddressInBytes[i] = (byte) (Integer.parseInt(serverIpComponents[i]));
		}

		return serverIpAddressInBytes;
	}
	
	public static <T> ArrayList<T> arrayToArrayList(T[] array){
		ArrayList<T> result = new ArrayList<T>();
		for(T element : array){
			result.add(element);
		}
		return result;
	}
	
	
}
