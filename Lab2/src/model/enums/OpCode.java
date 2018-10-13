package model.enums;

import model.errors.InvalidFormatException;

/** Enum for the Message Header's OPCODE field. */
public enum OpCode {
	/** a standard query */
	QUERY(0),
	/** an inverse query */
	IQUERY(1),
	/** a server status request */
	STATUS(2);
	public final int value;
	private OpCode(int value) {
		this.value = value;
	}
	public static OpCode fromString(String x) throws InvalidFormatException {
        switch(x) {
        case "0000":
            return QUERY;
        case "0001":
            return IQUERY;
        case "0010":
        	return STATUS;
        default:
        	throw new InvalidFormatException("The OPCODE field '" + x + "' is not recognized.");
        }
    }
}
