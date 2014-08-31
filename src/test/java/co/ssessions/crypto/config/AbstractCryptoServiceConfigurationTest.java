package co.ssessions.crypto.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import co.ssessions.crypto.config.helper.EmptyTestSecureSessionsModule;
import co.ssessions.crypto.config.helper.TestMapBasedCryptoServiceConfiguration;
import co.ssessions.crypto.config.helper.TestSecureSessionsModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class AbstractCryptoServiceConfigurationTest  extends TestCase {

	@Test
	public void test_initialize_constructor() {
		
		/*
		 * Method Under Test
		 */
		TestMapBasedCryptoServiceConfiguration defaultConfig = new TestMapBasedCryptoServiceConfiguration();
		
		assertNotNull(defaultConfig);
		assertTrue(defaultConfig instanceof TestMapBasedCryptoServiceConfiguration);
		
	}
	
	
	@Test
	public void test_initialize_dependency_injection() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		
		/*
		 * Method Under Test
		 */
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		
		assertNotNull(cryptoConfig);
		assertTrue(cryptoConfig instanceof TestMapBasedCryptoServiceConfiguration);
		
	}
	
	
	
	@Test
	public void test_get_Valid_1() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		String result = cryptoConfig.get("tester.crypto.someKey1");
		
		assertNotNull(result);
		assertThat(result, equalTo("someValueOne"));
		
	}
	
	
	@Test
	public void test_get_Valid_2() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		String result = cryptoConfig.get("tester.crypto.someKey2");
		
		assertNotNull(result);
		assertThat(result, equalTo("someValueTwo"));
	}
	
	
	@Test
	public void test_get_Valid_3() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		String result = cryptoConfig.get("tester.crypto.someKey3");
		
		assertNotNull(result);
		assertThat(result, equalTo("someValueThree"));
	}
	
	
	@Test
	public void test_get_InValid_Parameter() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		String result = cryptoConfig.get("Invalid.Parameter");
		
		assertNull(result);
	}
	
	
	@Test
	public void test_get_null_Parameter() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		
		String parameter = null;
		boolean exceptionCaught = false;
		/*
		 * Method Under Test
		 */
		try {
			cryptoConfig.get(parameter);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
	}
	
	
	@Test
	public void test_getParameterNames_Valid() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		Set<String> result = cryptoConfig.getParameterNames();
		
		assertNotNull(result);
		assertThat(result, hasItems("tester.crypto.someKey1", "tester.crypto.someKey2", "tester.crypto.someKey3"));
	}
	
	
	
	/* ************************************************************************
	 *  Test with Empty Configuration Parameter Map
	 * ************************************************************************/
	
	/*
	 * Ensure that even if the configuration map is empty that the get method returns a null.
	 * 
	 * This test just ensures implementing classes respect the contract.
	 */
	@Test
	public void test_get_EmptyConfiguration() {
		
		Injector injector = Guice.createInjector(new TestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		String result = cryptoConfig.get("Random.Parameter");
		
		assertNull(result);
	}
	
	
	/*
	 * Ensure that even if the configuration map is empty that the getParameters method returns an empty Set.
	 * 
	 * This test just ensures implementing classes respect the contract.
	 */
	@Test
	public void test_getParameterNames_EmptyConfiguration() {
		
		Injector injector = Guice.createInjector(new EmptyTestSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		Set<String> result = cryptoConfig.getParameterNames();
		
		assertNotNull(result);
		assertThat(result.size(), equalTo(0));
	}
	

}
