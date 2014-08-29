package co.ssessions.conf;

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.hamcrest.core.IsNot;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.TestCase;

public class EnvironmentConfigurationTest extends TestCase {

	
	@Test
	public void testGet() {
		
		System.setProperty("build.env", "local_test");
		/*
		 * Method under test
		 */
		Configuration config = EnvironmentConfiguration.get();
		assertNotNull(config);
		
		String result = config.getString("embedded_tomcat.baseFolder");
		
		assertNotNull(result);
		assertThat("src/test/resources/embedded_tomcat", equalTo(result));
		
	}
	
	
	
	@Test
	public void testGetString() {
		
		System.setProperty("build.env", "local_test");
		/*
		 * Method under test
		 */
		String result = EnvironmentConfiguration.get("embedded_tomcat.baseFolder");
		
		assertNotNull(result);
		assertThat("src/test/resources/embedded_tomcat", equalTo(result));
		
	}
	
	@Test
	public void testLoad() {
		
		System.setProperty("build.env", "local_test");
		/*
		 * Method under test
		 */
		EnvironmentConfiguration.load("couchbase_manager_conf.properties");
		
		String result = EnvironmentConfiguration.get("ss.applicationId");
		
		assertNotNull(result);
		assertThat("EmbeddedTomcatApplication", equalTo(result));
		
	}
	
	
	
}
