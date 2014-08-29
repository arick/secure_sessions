package co.ssessions.couchbase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;

public class CouchbaseUtils {

	public CouchbaseUtils() {
		// TODO Auto-generated constructor stub
	}
	
	
	
    public static void storeSession() throws IOException {
    	
    	
    	
    	
    }
	
    
    
    public static void main(String[] args) {
    	
    	
        // (Subset) of nodes in the cluster to establish a connection
        List<URI> hosts = new ArrayList<URI>();
        try {
			hosts.add(new URI("http://54.210.195.85:8091/pools"));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
        
        
        // Name of the Bucket to connect to
        String bucket = "default";
        
        // Password of the bucket (empty) string if none
        String password = "";
        
        // Connect to the Cluster
        CouchbaseClient client = null;
		try {
			CouchbaseConnectionFactory cf = new
	        		CouchbaseConnectionFactory(hosts, bucket, password);
			client = new CouchbaseClient(cf);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
     
        // Store a Document
        try {
        	
			client.set("my-first-document", "Hello Couchbase!").get();
			
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (ExecutionException ee) {
			ee.printStackTrace();
		}
     
        // Retreive the Document and print it
        System.out.println("\n\n\n" + client.get("my-first-document") + "\n\n\n\n\n");
     
        // Shutting down properly
        client.shutdown();
    	
    }

} // END CouchbaseUtils Class
