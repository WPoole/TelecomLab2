package utils;

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
}
