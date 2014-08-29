package co.ssessions.couchbase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import co.ssessions.conf.EnvConfig;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.google.inject.Inject;

public class CouchbaseClientHolder {

	private static volatile CouchbaseClient couchbaseClient;
	
	private String hosts;
	private String bucket;
	private String password;
	
	
	@Inject
	public CouchbaseClientHolder() {
		
		this.hosts = EnvConfig.getSafe("couchbase.hosts");
		this.bucket = EnvConfig.getSafe("couchbase.bucket");
		this.password = EnvConfig.getSafe("couchbase.password");
		
		couchbaseClient = this.getInstance();
	}
	
	

	public CouchbaseClient getInstance() {
		CouchbaseClient result = couchbaseClient;
		if (result == null) {
			synchronized (this) {
				result = couchbaseClient;
				if (result == null) {
					couchbaseClient = result = this.init();
				}
			}
		}
		return result;
	}
	
	
	private CouchbaseClient init() {
		
		// TODO: Defensive programming here - ensure all of parameters have been passed in.
		
		
		
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
		
		
		CouchbaseClient client = null;
		try {
			CouchbaseConnectionFactory cf = new CouchbaseConnectionFactory(hostsURIList, this.bucket, this.password);
			client = new CouchbaseClient(cf);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return client;
		
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
