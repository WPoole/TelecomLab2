import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import model.Message;
import model.enums.Type;
import model.errors.InvalidFormatException;
import model.records.ResourceRecord;
import utils.Conversion;

@RunWith(Theories.class)
public class DnsClientTest {
	public static String dnsServerIP = "@8.8.8.8";
	
	@DataPoints
	public static String[] SIMPLE_QUERY = new String[] {
			"www.google.com",
			"www.facebook.com",
			"www.yahoo.com",
			"www.github.com",
			"www.youtube.com",
			"www.amazon.com",
			"www.bloomberg.com",			
	};
	
	
	
	@Theory
	public void typeAQuery(String hostName) {
		String[] args = {dnsServerIP, hostName};
		
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
		response.printToConsole();
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
	
	@Theory
	public void typeAQueryWithRetries(String hostname) {
		
	}
	
	@Theory
	public void typeMXQuery(String hostName) {
		String[] args = {"-mx", dnsServerIP, hostName};
		Message response;
		try {
			response = DnsClient.performDNSRequest(args);
		} catch (IOException | InvalidFormatException e) {
			System.err.println(e);
			e.printStackTrace();
			fail();
			return;
		}
		response.printToConsole();
	}
		
}
