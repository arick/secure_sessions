package co.ssessions.couchbase;

import junit.framework.TestCase;

import org.junit.Test;

import co.ssessions.conf.EnvConfig;
import co.ssessions.modules.CouchbaseSecureSessionsModule;

import com.couchbase.client.CouchbaseClient;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CouchbaseClientHolderTest extends TestCase {

	@Test
	public void testGetInstance() {
		
		Injector injector = Guice.createInjector(new CouchbaseSecureSessionsModule());
		CouchbaseClientHolder cch = injector.getInstance(CouchbaseClientHolder.class);
		
		assertNotNull(cch);
		
		cch.setHosts(EnvConfig.getSafe("couchbase.hosts"));
		cch.setBucket(EnvConfig.getSafe("couchbase.bucket"));
		cch.setPassword(EnvConfig.getSafe("couchbase.password"));
		
		CouchbaseClient client = cch.getInstance();
		
		assertNotNull(client);
		assertTrue(client instanceof CouchbaseClient);
		
		
	}

}
