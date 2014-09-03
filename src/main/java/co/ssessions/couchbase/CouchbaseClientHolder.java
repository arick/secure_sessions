package co.ssessions.couchbase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import co.ssessions.conf.EnvConfig;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;

public class CouchbaseClientHolder {

	private String hosts;
	private String bucket;
	private String password;
	
	private static volatile CouchbaseClient singleton = null;
	

	public CouchbaseClientHolder() {
		
		this.hosts = EnvConfig.getSafe("couchbase.hosts");
		this.bucket = EnvConfig.getSafe("couchbase.bucket");
		this.password = EnvConfig.getSafe("couchbase.password");
		
	}
	
	
	public final CouchbaseClient getInstance() {
		CouchbaseClient proxy = singleton;
		if (proxy == null) {
			synchronized (this) {
				proxy = singleton;
				if (proxy == null) {
					singleton = proxy = this.init();
				}
			}
		}
		return proxy;
	}
	
	
	public final void nullifySingleton() {
		singleton = null;
	}
	
	public static void clearSingleton() {
		new EnvConfig().nullifySingleton();
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


}
