package model;

public class Message {
	public String header;
	public Question question;
	public ResourceRecord[] answer;
	public ResourceRecord[] authority;
	public ResourceRecord[] additional;
	
	public Message(){
		// TODO
	}
	
	
	
	/**This is the format of the message header:
	 *  					           1  1  1  1  1  1  
	 *  0   1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                     ID                        |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |QR|  Opcode   |AA|TC|RD|RA|  Z     |   RCODE   |      
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   QDCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   ANCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   NSCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 *  |                   ARCOUNT                     |  
	 *  +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 */
	public class Header {
		/** A 16 bit identifier assigned by the program that
		 * generates any kind of query. This identifier is copied 
		 * the corresponding reply and can be used by the requester 
		 * to match up replies to outstanding queries.
		 */
		public int ID;
		/** A one bit field that specifies whether this message is a
		 *  query (0), or a response (1).
		 */
		public boolean QR;
		/** Authoritative Answer - this bit is valid in responses,
		 * and specifies that the responding name server is an
		 * authority for the domain name in question section.
		 */
		public boolean AA;	
		public Header(){
			// TODO		
		}		
	}

	/** Enum for the Message Header's OPCODE field. */
	enum OPCODE {
		/**a standard query*/
		QUERY,
		/**an inverse query*/
		IQUERY,
		/**a server status request*/
		STATUS,
		RESERVED;
	}
	
	public class Question{
		QType queryType;
		QClass queryClass;
		String queryDomainName;
	}
}
