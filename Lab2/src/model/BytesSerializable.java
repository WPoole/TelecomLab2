package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import model.errors.InvalidFormatException;

public interface BytesSerializable {
	public List<Byte> toBytes();
	default public byte[] toByteArray() {
		List<Byte> bytesList = this.toBytes();
		byte[] byteArray = new byte[bytesList.size()];
		for(int i = 0; i < bytesList.size(); i++) {
			byteArray[i] = bytesList.get(i);
		}
		return byteArray;
	}
	/**
	 * Populate the instance using the bytes provided.
	 * @param bytes Bytes to use to populate the instance.
	 */
	public void fromBytes(byte[] bytes);
}
