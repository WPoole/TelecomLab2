package model;

public enum QType {
	/** A request for a transfer of an entire zone */
	AXFR (252),
	/** A request for mailbox-related records (MB, MG or MR) */
	MAILB (253),
	/** A request for mail agent RRs (Obsolete - see MX) */
	MAILA (254),
	/** ('*') 255 A request for all records */
	STAR (255);
	public final int value;
	private QType(int value){
		this.value = value;
	}
}
