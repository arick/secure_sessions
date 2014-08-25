package co.ssessions.conf;

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import junit.framework.TestCase;

public class EnvironmentConfigurationTest extends TestCase {

	
	@Test
	public void testGet() {
		
		System.setProperty("build.env", "local_test");
		/*
		 * Method under test
		 */
		Configuration config = EnvironmentConfiguration.get();
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
		String result = EnvironmentConfiguration.get("couchbase.hosts");
		
		assertNotNull(result);
		assertEquals("http://localhost:8091/pools", result);
		
	}
	
	
	
}
