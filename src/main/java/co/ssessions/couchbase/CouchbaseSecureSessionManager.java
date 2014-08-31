package co.ssessions.couchbase;

import org.apache.catalina.LifecycleException;
import org.apache.commons.lang3.StringUtils;

import co.ssessions.managers.SecureSessionManagager;

import com.couchbase.client.CouchbaseClient;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CouchbaseSecureSessionManager extends SecureSessionManagager {

	protected static final String name = "CouchbaseSecureSessionManager";
    protected static final String info = name + "/1.0";
	
	
    /*
     * Values injected from the context.xml file
     */
	protected String hosts;
	protected String bucket = "default";
	protected String password = "";
	
	
	public CouchbaseSecureSessionManager() {
		super();
	}


	@Override
	protected void initInternal() throws LifecycleException {

		// Establish and configure the CouchbaseClientHolder's static CouchbaseClient
//		Injector injector = Guice.createInjector(new CouchbaseSecureSessionsModule());
//		CouchbaseClientHolder couchbaseClientHolder = injector.getInstance(CouchbaseClientHolder.class);
//
//		couchbaseClientHolder.setHosts(this.getSafe(this.hosts));
//		couchbaseClientHolder.setBucket(this.getSafe(this.bucket));
//		couchbaseClientHolder.setPassword(this.getSafe(this.password));
//		
//		// Just needed to initialize the static CouchbaseClient
//		@SuppressWarnings("unused")
//		CouchbaseClient client = couchbaseClientHolder.getInstance();
		
		super.initInternal();
		
	} // END initInternal Method


	
}
