package co.ssessions.conf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

public class EnvConfigTest  extends TestCase {

	public void tearDown() {
		
		EnvConfig.clearSingleton();
	}
	
	
	@Test
	public void testLoad_Valid() {
		
		System.setProperty("build.env", "unit_test");
		String managerConfigFilePath = EnvConfig.getSafe("tomcat_manager_conf.propertiesFilePath");

		
		/*
		 * Method under test
		 */
		EnvConfig.load(managerConfigFilePath);
		
		String result = EnvConfig.get("ss.applicationId");
		
		assertNotNull(result);
		assertThat("EmbeddedTomcatApplication", equalTo(result));
		
		
		
	}
	
	
	@Test
	public void testLoad_Null() {
		
		System.setProperty("build.env", "unit_test");
		
		String propertiesFilePath = null;
		boolean exceptionCaught = false;
		
		/*
		 * Method under test
		 */
		try {
			EnvConfig.load(propertiesFilePath);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
	}
	
	
	@Test
	public void testGet_Valid() {
		
		System.setProperty("build.env", "unit_test");
		
		/*
		 * Method under test
		 */
		Configuration config = EnvConfig.get();
		assertNotNull(config);
		
		String result = config.getString("embedded_tomcat.baseFolder");
		
		assertNotNull(result);
		assertThat("src/test/resources/embedded_tomcat", equalTo(result));
		
	}
	
	
	@Test
	public void testGet_String_Valid() {
		
		System.setProperty("build.env", "unit_test");
		
		/*
		 * Method under test
		 */
		String result = EnvConfig.get("embedded_tomcat.baseFolder");
		
		assertNotNull(result);
		assertThat("src/test/resources/embedded_tomcat", equalTo(result));
		
	}
	
	
	@Test
	public void testGet_String_Null() {
		
		System.setProperty("build.env", "unit_test");
		
		String parameter = null;
		boolean exceptionCaught = false;
		/*
		 * Method under test
		 */
		try {
			String result = EnvConfig.get(parameter);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}
	
	
	@Test
	public void testGetSafe_String_Valid() {
		
		System.setProperty("build.env", "unit_test");
		
		/*
		 * Method under test
		 */
		String result = EnvConfig.getSafe("embedded_tomcat.baseFolder");
		
		assertNotNull(result);
		assertThat("src/test/resources/embedded_tomcat", equalTo(result));
		
	}
	
	
	public void testGetSafe_String_Invalid() {
		
		System.setProperty("build.env", "unit_test");
		
		/*
		 * Method under test
		 */
		String result = EnvConfig.getSafe("Invalid.Parameter");
		
		assertNotNull(result);
		assertThat("", equalTo(result));
		
	}
	
	
	@Test
	public void testGetSafe_String_Null() {
		
		System.setProperty("build.env", "unit_test");
		
		String property = null;
		boolean exceptionCaught = false;
		/*
		 * Method under test
		 */
		try {
			String result = EnvConfig.getSafe(property);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}	
	
	
	@Test
	public void testGetStringArray_String_Valid() {
		
		System.setProperty("build.env", "unit_test");
		
		/*
		 * Method under test
		 */
		String[] results = EnvConfig.getStringArray("array.validHosts");
		
		assertNotNull(results);
		assertThat(3, equalTo(results.length));
		
		String[] expected = {"value 1", "value 2", "value 3"};
		assertThat(Arrays.asList(results), hasItems(expected));
		
	}
	
	
	@Test
	public void testGetStringArray_String_InValid() {
		
		System.setProperty("build.env", "unit_test");
		
		/*
		 * Method under test
		 */
		String[] results = EnvConfig.getStringArray("Invalid.Parameter");
		
		assertNull(results);
		
	}
	
	@Test
	public void testGetStringArray_String_EmptyProperty() {
		
		System.setProperty("build.env", "unit_test");
		
		/*
		 * Method under test
		 */
		String[] results = EnvConfig.getStringArray("array.emptyHosts");
		
		assertNotNull(results);
		assertThat(0, equalTo(results.length));
		
	}
	
	@Test
	public void testGetStringArray_String_Null() {
		
		System.setProperty("build.env", "unit_test");
		
		String property = null;
		boolean exceptionCaught = false;
		/*
		 * Method under test
		 */
		try {
			String[] results = EnvConfig.getStringArray(property);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}
	
	
	
}
