package co.ssessions.crypto.config.impl;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import junit.framework.TestCase;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import co.ssessions.conf.EnvConfig;
import co.ssessions.couchbase.CouchbaseSecureSessionsModule;
import co.ssessions.crypto.config.CryptoServiceConfiguration;
import co.ssessions.crypto.config.impl.helper.TestDefaultSecureSessionsModule;
import co.ssessions.store.SecureSessionsStoreBase;

public class DefaultCryptoServiceConfigurationTest  extends TestCase {

	public void setUp() {
		
		System.setProperty("build.env", "unit_test");
		String managerConfigFilePath = EnvConfig.getSafe("tomcat_manager_conf.propertiesFilePath");
		EnvConfig.load(managerConfigFilePath);
	}

	public void tearDown() {
		EnvConfig.clearSingleton();
	}

	@Test
	public void test_initialize_constructor() {
		
		/*
		 * Method Under Test
		 */
		DefaultCryptoServiceConfiguration defaultConfig = new DefaultCryptoServiceConfiguration();
		
		assertNotNull(defaultConfig);
		assertTrue(defaultConfig instanceof DefaultCryptoServiceConfiguration);
		
	}
	
	
	@Test
	public void test_initialize_dependency_injection() {
		
		Injector injector = Guice.createInjector(new TestDefaultSecureSessionsModule());
		
		/*
		 * Method Under Test
		 */
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		
		assertNotNull(cryptoConfig);
		assertTrue(cryptoConfig instanceof DefaultCryptoServiceConfiguration);
		
	}
	
	
	
	@Test
	public void test_get_Valid() {
		
		Injector injector = Guice.createInjector(new TestDefaultSecureSessionsModule());
		CryptoServiceConfiguration<String, String> cryptoConfig = injector.getInstance(Key.get(new TypeLiteral<CryptoServiceConfiguration<String,String>>(){}));
		
		/*
		 * Method Under Test
		 */
		String result = cryptoConfig.get("crypto.privateKeyFilePath");
		
		assertNotNull(result);
		assertThat(result, equalTo("src/test/resources/crypto/privkey.pkcs8.pem"));
		
	}
	


}
