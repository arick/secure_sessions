package co.ssessions.couchbase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import net.spy.memcached.PersistTo;

import org.junit.Test;

import co.ssessions.SessionKey;
import co.ssessions.SessionModel;
import co.ssessions.conf.EnvConfig;
import co.ssessions.json.DateJsonDeserializer;
import co.ssessions.json.DateJsonSerializer;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class CouchbaseDBTest extends TestCase {


	CouchbaseClient couchbaseClient = null;
	
	public void setUp() {
		
		List<URI> hosts = new ArrayList<URI>();
        try {
        	String[] hostStringArray = EnvConfig.getStringArray("couchbase.hosts");
        	for (String hostString : hostStringArray) {
        		hosts.add(new URI(hostString));
        	}
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
        
        // Name of the Bucket to connect to
        String bucket = EnvConfig.getSafe("couchbase.bucket");
        
        // Password of the bucket (empty) string if none
        String password = EnvConfig.getSafe("couchbase.password");
        
        // Connect to the Cluster
		try {
			CouchbaseConnectionFactory cf = new
			CouchbaseConnectionFactory(hosts, bucket, password);
			this.couchbaseClient = new CouchbaseClient(cf);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	} // END setUp Metod
	
	
	
	public void tearDown() {
		this.couchbaseClient.shutdown();
		this.couchbaseClient = null;
	}
	
	
	
	@Test
	public void testStoreSession() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = new SessionKey("WonderApp", "1234abcd");
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		sessionModel.setData(data);
		
		/*
		 * Method Under Test
		 */
		CouchbaseDB.storeSession(this.couchbaseClient, sessionKey, sessionModel);
		
		/*
		 * Check results are in database
		 */
		String resultJson = (String) this.couchbaseClient.get(sessionKey.toString());
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonDeserializer()).create();
		
		SessionModel resultSessionModel = gson.fromJson(resultJson, SessionModel.class);
		assertNotNull(resultSessionModel);
		
		assertEquals(updatedDate, resultSessionModel.getUpdateTime());
		assertEquals(createdDate, resultSessionModel.getCreateTime());
		assertEquals(data, resultSessionModel.getData());
		assertEquals(SessionModel.BASE64, resultSessionModel.getDataEncoding());
		assertEquals("08_17_2014", resultSessionModel.getPrivateKeyId());
		
		// Remove data from database
		this.couchbaseClient.delete(sessionKey.toString());
	}
	
	
	
	@Test
	public void testDeleteSession() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = new SessionKey("WonderApp", "1234abcd");
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		sessionModel.setData(data);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		
		// Put SessionJSON into the database
		this.couchbaseClient.set(sessionKey.toString(), sessionModelJson, PersistTo.ONE);
		
		// Ensure that it really is in the database
		String sessionModelJsonFromDB = (String) this.couchbaseClient.get(sessionKey.toString());
		assertNotNull(sessionModelJsonFromDB);
		
		/*
		 * Method Under Test
		 */
		CouchbaseDB.deleteSession(this.couchbaseClient, sessionKey);
		
		/*
		 * Ensure that the results are NOT in database
		 */
		String resultSessionModelJson = (String) this.couchbaseClient.get(sessionKey.toString());
		assertNull(resultSessionModelJson);
		
	}
	
	@Test
	public void testDeleteSessions() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey1 = new SessionKey("WonderApp", "1234abcd");
		SessionKey sessionKey2 = new SessionKey("WonderApp", "abcd1234");
		SessionKey sessionKey3 = new SessionKey("WonderApp", "1a2b3c4d");
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		sessionModel.setData(data);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		
		// Put SessionJSON into the database
		this.couchbaseClient.set(sessionKey1.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey2.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey3.toString(), sessionModelJson, PersistTo.ONE);
		
		// Ensure that it really is in the database
		String sessionModelJsonFromDB1 = (String) this.couchbaseClient.get(sessionKey1.toString());
		assertNotNull(sessionModelJsonFromDB1);
		String sessionModelJsonFromDB2 = (String) this.couchbaseClient.get(sessionKey2.toString());
		assertNotNull(sessionModelJsonFromDB2);
		String sessionModelJsonFromDB3 = (String) this.couchbaseClient.get(sessionKey3.toString());
		assertNotNull(sessionModelJsonFromDB3);
		
		/*
		 * Method Under Test
		 */
		Set<SessionKey> sessionKeys = new HashSet<SessionKey>();
		sessionKeys.add(sessionKey1);
		sessionKeys.add(sessionKey2);
		sessionKeys.add(sessionKey3);
		CouchbaseDB.deleteSessions(this.couchbaseClient, sessionKeys);
		
		/*
		 * Ensure that the results are NOT in database
		 */
		String resultSessionModelJson1 = (String) this.couchbaseClient.get(sessionKey1.toString());
		assertNull(resultSessionModelJson1);
		String resultSessionModelJson2 = (String) this.couchbaseClient.get(sessionKey2.toString());
		assertNull(resultSessionModelJson2);
		String resultSessionModelJson3 = (String) this.couchbaseClient.get(sessionKey3.toString());
		assertNull(resultSessionModelJson3);
		
	}
	
	
	@Test
	public void testRetrieveSession() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = new SessionKey("WonderApp", "1234abcd");
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		sessionModel.setData(data);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		
		// Put SessionJSON into the database
		this.couchbaseClient.set(sessionKey.toString(), sessionModelJson, PersistTo.ONE);
		
		// Ensure that it really is in the database
		String sessionModelJsonFromDB = (String) this.couchbaseClient.get(sessionKey.toString());
		assertNotNull(sessionModelJsonFromDB);
		
		/*
		 * Method Under Test
		 */
		SessionModel resultSessionModel = CouchbaseDB.retrieveSession(this.couchbaseClient, sessionKey);
		
		
		assertEquals(updatedDate, resultSessionModel.getUpdateTime());
		assertEquals(createdDate, resultSessionModel.getCreateTime());
		assertEquals(data, resultSessionModel.getData());
		assertEquals(SessionModel.BASE64, resultSessionModel.getDataEncoding());
		assertEquals("08_17_2014", resultSessionModel.getPrivateKeyId());
		
		// Remove data from database
		this.couchbaseClient.delete(sessionKey.toString());
		
	}
	
	
	@Test
	public void testNumberOfSession_1() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey1 = new SessionKey("WonderApp1", "1234abcd");
		SessionKey sessionKey2 = new SessionKey("WonderApp1", "abcd1234");
		SessionKey sessionKey3 = new SessionKey("WonderApp2", "1a2b3c4d");
		SessionKey sessionKey4 = new SessionKey("WonderApp1", "1a2e3c4d");
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		sessionModel.setData(data);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		
		// Put SessionJSON into the database
		this.couchbaseClient.set(sessionKey1.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey2.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey3.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey4.toString(), sessionModelJson, PersistTo.ONE);
		
		// Ensure that it really is in the database
		String sessionModelJsonFromDB1 = (String) this.couchbaseClient.get(sessionKey1.toString());
		assertNotNull(sessionModelJsonFromDB1);
		String sessionModelJsonFromDB2 = (String) this.couchbaseClient.get(sessionKey2.toString());
		assertNotNull(sessionModelJsonFromDB2);
		String sessionModelJsonFromDB3 = (String) this.couchbaseClient.get(sessionKey3.toString());
		assertNotNull(sessionModelJsonFromDB3);
		String sessionModelJsonFromDB4 = (String) this.couchbaseClient.get(sessionKey4.toString());
		assertNotNull(sessionModelJsonFromDB4);
		
		/*
		 * Method Under Test
		 */
		long count = CouchbaseDB.numberOfSessions(this.couchbaseClient, sessionKey1);
		
		assertEquals(3, count);
		
		this.couchbaseClient.delete(sessionKey1.toString());
		this.couchbaseClient.delete(sessionKey2.toString());
		this.couchbaseClient.delete(sessionKey3.toString());
		this.couchbaseClient.delete(sessionKey4.toString());
		
	} // END testNumberOfSession Method
	
	
	@Test
	public void testNumberOfSession_2() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey1 = new SessionKey("WonderApp4", "1234abcd");
		SessionKey sessionKey2 = new SessionKey("WonderApp4", "abcd1234");
		SessionKey sessionKey3 = new SessionKey("WonderApp3", "1a2b3c4d");
		SessionKey sessionKey4 = new SessionKey("WonderApp4", "1a2e3c4d");
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		sessionModel.setData(data);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		
		// Put SessionJSON into the database
		this.couchbaseClient.set(sessionKey1.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey2.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey3.toString(), sessionModelJson, PersistTo.ONE);
		this.couchbaseClient.set(sessionKey4.toString(), sessionModelJson, PersistTo.ONE);
		
		// Ensure that it really is in the database
		String sessionModelJsonFromDB1 = (String) this.couchbaseClient.get(sessionKey1.toString());
		assertNotNull(sessionModelJsonFromDB1);
		String sessionModelJsonFromDB2 = (String) this.couchbaseClient.get(sessionKey2.toString());
		assertNotNull(sessionModelJsonFromDB2);
		String sessionModelJsonFromDB3 = (String) this.couchbaseClient.get(sessionKey3.toString());
		assertNotNull(sessionModelJsonFromDB3);
		String sessionModelJsonFromDB4 = (String) this.couchbaseClient.get(sessionKey4.toString());
		assertNotNull(sessionModelJsonFromDB4);
		
		/*
		 * Method Under Test
		 */
		long count = CouchbaseDB.numberOfSessions(this.couchbaseClient, sessionKey3);
		
		assertEquals(1, count);
		
		this.couchbaseClient.delete(sessionKey1.toString());
		this.couchbaseClient.delete(sessionKey2.toString());
		this.couchbaseClient.delete(sessionKey3.toString());
		this.couchbaseClient.delete(sessionKey4.toString());
		
	} // END testNumberOfSession_2 Method

} // END CouchbaseDBTest Class
