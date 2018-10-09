package model.enums;

import model.errors.InvalidFormatException;

public enum ResponseCode {
	/** No error condition */
	NO_ERROR,
	/**
	 * The name server was unable to interpret the query
	 */
	FORMAT_ERROR,
	/**
	 * The name server was unable to process this query due to a problem
	 * with the name server.
	 */
	SERVER_FAILURE,
	/**
	 * Meaningful only for responses from an authoritative name server, this
	 * code signifies that the domain name referenced in the query does not
	 * exist.
	 */
	NAME_ERROR,
	/**
	 * The name server does not support the requested kind of query.
	 */
	NOT_IMPLEMENTED,
	/**
	 * The name server refuses to perform the specified operation for policy
	 * reasons. For example, a name server may not wish to provide the
	 * information to the particular requester, or a name server may not
	 * wish to perform a particular operation (e.g., zone transfer) for
	 * particular data.
	 */
	REFUSED;
	
	public static ResponseCode fromString(String x) throws InvalidFormatException {
        switch(x) {
	        case "0000":
	            return NO_ERROR;
	        case "0001":
	        	return FORMAT_ERROR;
	        case "0010":
	        	return SERVER_FAILURE;
	        case "0011":
	        	return NAME_ERROR;
	        case "0100":
	        	return NOT_IMPLEMENTED;
	        case "0101":
	        	return REFUSED;
	        default:
	        	throw new InvalidFormatException("The Response code '" + x + "' is not recognized.");
        }
    }
	
}