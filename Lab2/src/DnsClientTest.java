import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import model.Message;
import model.enums.Type;
import model.errors.InvalidFormatException;
import model.records.ResourceRecord;
import utils.Conversion;

public class DnsClientTest {
		
	@Ignore("Testing only one thing at a time.")
	@Test()
	public void testSimpleTypeAQuery() {
		String[] args = {"@8.8.8.8", "www.mcgill.ca"};
		
		Message response;
		InetAddress expected;
		try {
			response = DnsClient.performDNSRequest(args);
			expected = Inet4Address.getByName("www.mcgill.ca");
		} catch (IOException | InvalidFormatException e) {
			System.err.println(e);
			e.printStackTrace();
			fail();
			return;
		}
		
		for(ResourceRecord rr : response.answer) {
			rr.printToConsole();
		}
		assertTrue(response.answer.length >= 1);
		ResourceRecord rr = response.answer[0];
		// we should get a Type A record.
		assertEquals(rr.TYPE, Type.A);
		assertArrayEquals(expected.getAddress(), rr.RDATA);	
	}
	
	@Test
	public void testYahooCaTypeA() {
		String hostName = "www.facebook.com";
		String[] args = {"@8.8.8.8", hostName};
		
		Message response;
		InetAddress expected;
		try {
			response = DnsClient.performDNSRequest(args);
			expected = Inet4Address.getByName(hostName);
		} catch (IOException | InvalidFormatException e) {
			System.err.println(e);
			e.printStackTrace();
			fail();
			return;
		}
		DnsClient.printOutMessage(response);
		System.out.println("(Expected: " + Conversion.ipBytesToString(expected.getAddress()) + ")");

		// we should get at least one answer.
		assertTrue(response.answer.length >= 1);
		for(ResourceRecord rr : response.answer) {
			// we should get at least one Type A record.
			if (rr.TYPE == Type.A && Arrays.equals(expected.getAddress(), rr.RDATA)) {
				return;
			}
		}
		fail();
	}
		
}
