package co.ssessions.couchbase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.LifecycleException;

import co.ssessions.SecureSessionManagager;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;

public class CouchbaseSecureSessionManager extends SecureSessionManagager {

	protected static final String name = "CouchbaseSecureSessionManager";
    protected static final String info = name + "/1.0";
	
	protected final CouchbaseSessionStore couchBaseSessionStore;
	protected CouchbaseClient couchbaseClient;
	
    /*
     * Values injected from the context.xml file
     */
	protected String hosts;
	protected String bucket = "default";
	protected String password = "";
	
	
	public CouchbaseSecureSessionManager() {
		
		super();
		
		this.couchBaseSessionStore = new CouchbaseSessionStore();
		this.setStore(this.couchBaseSessionStore);
	
	}


	@Override
	protected void initInternal() throws LifecycleException {
		super.initInternal();
		
		// Set up the Couchbase Client
		List<URI> hostsURIList = new ArrayList<URI>();
		String[] hostsURIArray = this.hosts.split(",");
		for (String hostUriString : hostsURIArray) {
			
			URI uri = null;
			
			try {
				uri = new URI(hostUriString);
			} catch (URISyntaxException use) {
				use.printStackTrace();
			}
			
			hostsURIList.add(uri);
		}
		
		
		try {
			CouchbaseConnectionFactory cf = new CouchbaseConnectionFactory(hostsURIList, this.bucket, this.password);
			this.couchbaseClient = new CouchbaseClient(cf);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		this.couchBaseSessionStore.setCouchbaseClient(this.couchbaseClient);
		this.couchBaseSessionStore.setCryptoService(this.cryptoService);
		this.couchBaseSessionStore.setApplicationId(this.applicationId);
		
		
	} // END initInternal Method


	/**
	 * Coma delimited list of URIs in the format 
	 * 
	 * "http://cb1.yourdomain.com:8901/pools,http://cb2.yourdomain.com:8901/pools"
	 * 
	 * @param hosts
	 */
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}


	public void setBucket(String bucket) {
		this.bucket = bucket;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
}
