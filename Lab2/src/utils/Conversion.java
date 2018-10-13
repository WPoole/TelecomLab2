package utils;

import java.util.ArrayList;

import model.enums.*;

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
	public static String binaryString(OpCode opcode) {
		switch(opcode) {
			case QUERY:
				return "0000";
			case IQUERY:
				return "0001";
			case STATUS:
				return "0010";
			default:
				return "0000";
		}
	}
	public static String binaryString(ResponseCode rcode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static byte[] ipAddressStringToByteArray(String ipAddressString) {
		String[] serverIpComponents = ipAddressString.split(".");
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
