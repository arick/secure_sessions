package co.ssessions.couchbase;

import junit.framework.TestCase;

import org.junit.Test;

import co.ssessions.conf.EnvConfig;

import com.couchbase.client.CouchbaseClient;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CouchbaseClientHolderTest extends TestCase {

	public void tearDown () {
		CouchbaseClientHolder.clearSingleton();
	}
	
	@Test
	public void testGetInstance() {
		
		String managerConfigFilePath = EnvConfig.getSafe("tomcat_manager_conf.propertiesFilePath");
		EnvConfig.load(managerConfigFilePath);
		
		Injector injector = Guice.createInjector(new CouchbaseSecureSessionsModule());
		CouchbaseClientHolder cch = injector.getInstance(CouchbaseClientHolder.class);
		
		assertNotNull(cch);
		
		/*
		 * Method under test
		 */
		CouchbaseClient client = cch.getInstance();
		
		assertNotNull(client);
		assertTrue(client instanceof CouchbaseClient);

	}

}
