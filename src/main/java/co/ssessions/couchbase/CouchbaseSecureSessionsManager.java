package co.ssessions.couchbase;

import org.apache.catalina.LifecycleException;

import co.ssessions.managers.SecureSessionsManagager;

public class CouchbaseSecureSessionsManager extends SecureSessionsManagager {

	protected static final String name = "CouchbaseSecureSessionsManager";
    protected static final String info = name + "/1.0";
	
	
    /*
     * Values injected from the context.xml file
     */
	protected String hosts;
	protected String bucket = "default";
	protected String password = "";
	
	
	public CouchbaseSecureSessionsManager() {
		super();
	}


	@Override
	protected void initInternal() throws LifecycleException {

		super.initInternal();
		
	} // END initInternal Method
	
}
