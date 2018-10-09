package model.enums;

public enum QueryClass {
	/** the Internet*/
	IN (1),
	/** the CSNET class (Obsolete - used only for examples in
	some obsolete RFCs) */
	CS (2),
	/**
	 * the CHAOS class
	 */
	CH (3),
	/**
	 * Hesiod [Dyer 87]
	 */
	HS (4),
	ALL (255);
	public final int value;
	private QueryClass(int value){
		this.value = value;
	}
}