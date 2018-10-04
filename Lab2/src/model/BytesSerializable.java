package model;

import java.util.Collection;
import java.util.List;

public interface BytesSerializable {
	public List<Byte> toBytes();
	/**
	 * Populate the instance using the bytes provided.
	 * @param bytes Bytes to use to populate the instance.
	 */
	public void fromBytes(byte[] bytes);
}
