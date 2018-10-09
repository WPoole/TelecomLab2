package model.enums;

import model.errors.InvalidFormatException;

/** Enum for the Message Header's OPCODE field. */
public enum OpCode {
	/** a standard query */
	QUERY,
	/** an inverse query */
	IQUERY,
	/** a server status request */
	STATUS;
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
