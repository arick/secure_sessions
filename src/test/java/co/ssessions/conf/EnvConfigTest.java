package co.ssessions.conf;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

public class EnvConfigTest  extends TestCase {

	
	@Test
	public void testGet() {
		
		System.setProperty("build.env", "local_test");
		/*
		 * Method under test
		 */
		Configuration config = EnvConfig.get();
		String result = config.getString("couchbase.hosts");
		
		assertNotNull(result);
		assertEquals("http://localhost:8091/pools", result);
		
	}
	
	
	
	@Test
	public void testGetString() {
		
		System.setProperty("build.env", "local_test");
		/*
		 * Method under test
		 */
		String result = EnvConfig.get("couchbase.hosts");
		
		assertNotNull(result);
		assertEquals("http://localhost:8091/pools", result);
		
	}
	
	
}
