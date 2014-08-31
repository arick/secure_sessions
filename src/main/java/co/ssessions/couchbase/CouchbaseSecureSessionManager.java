package co.ssessions.couchbase;

import org.apache.catalina.LifecycleException;

import co.ssessions.managers.SecureSessionManagager;

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

		super.initInternal();
		
	} // END initInternal Method
	
}
