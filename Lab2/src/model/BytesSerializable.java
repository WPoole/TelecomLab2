package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface which describes objects that can be serialized into a sequence of bytes.
 * NOTE: One of toBytes() or toByteArray() must absolutely be implemented.
 */
public interface BytesSerializable{
	default public List<Byte> toBytes() {
		byte[] bytes = this.toByteArray();
		ArrayList<Byte> values = new ArrayList<>();
		for(byte b : bytes) {
			values.add(b);
		}
		return values;
	}
	default public byte[] toByteArray() {
		List<Byte> bytesList = this.toBytes();
		byte[] byteArray = new byte[bytesList.size()];
		for(int i = 0; i < bytesList.size(); i++) {
			byteArray[i] = bytesList.get(i);
		}
		return byteArray;
	}
}
