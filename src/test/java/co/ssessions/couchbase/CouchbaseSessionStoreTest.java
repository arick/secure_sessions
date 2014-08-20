package co.ssessions.couchbase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.junit.Test;

import co.ssessions.SessionKey;
import co.ssessions.SessionModel;
import co.ssessions.crypto.CryptoService;
import co.ssessions.crypto.PKCS8CryptoService;
import co.ssessions.json.DateJsonSerializer;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CouchbaseSessionStoreTest extends TestCase {

	CouchbaseClient couchbaseClient = null;
	
	public void setUp() {
		
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
		try {
			CouchbaseConnectionFactory cf = new
	        		CouchbaseConnectionFactory(hosts, bucket, password);
			this.couchbaseClient = new CouchbaseClient(cf);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	} // END setUp Metod
	
	
	public void tearDown() {
		this.couchbaseClient = null;
	} // END tearDown Method
	
	
	@Test
	public void testSave() {
	
		/*
		 * Fixtures
		 */
		HttpSession mockedHttpSession = mock(HttpSession.class);
		when(mockedHttpSession.getId()).thenReturn("The_Session_Id");
		when(mockedHttpSession.getAttribute("first_name")).thenReturn("Jasmine");
		when(mockedHttpSession.getAttribute("last_name")).thenReturn("Moore");
		when(mockedHttpSession.getAttribute("age")).thenReturn("4");
		
		Vector<String> attributeNames = new Vector<String>();
		attributeNames.addElement("first_name");
		attributeNames.addElement("last_name");
		attributeNames.addElement("age");
		when(mockedHttpSession.getAttributeNames()).thenReturn(attributeNames.elements());
		
		
		Session mockedSession = mock(Session.class);
		when(mockedSession.getSession()).thenReturn(mockedHttpSession);
		
		
		CouchbaseSessionStore couchbaseSessionStore = new CouchbaseSessionStore();
		couchbaseSessionStore.setCouchbaseClient(this.couchbaseClient);
		couchbaseSessionStore.setCryptoService(new PKCS8CryptoService("src/resources/privkey.pkcs8.pem"));
		couchbaseSessionStore.setApplicationId("WonderApp");

		
		/*
		 * Method Under Test
		 */
		try {
			couchbaseSessionStore.save(mockedSession);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Remove data from database
		SessionKey sessionKey = new SessionKey(couchbaseSessionStore.getApplicationId(), mockedHttpSession.getId());
		this.couchbaseClient.delete(sessionKey.toString());
		
		
	} // END testSave Method
	
	
	@Test
	public void testRemove() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = new SessionKey("WonderApp", "1234abcd");
		CryptoService cryptoService = new PKCS8CryptoService("src/resources/privkey.pkcs8.pem");
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		byte[] dataBytes = cryptoService.encrypt(data);
		byte[] dataBase64 = Base64.getEncoder().encode(dataBytes);
		sessionModel.setData(new String(dataBase64));
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
		String sessionModelJson = gson.toJson(sessionModel);
		
		// Put SessionJSON into the database
		this.couchbaseClient.set(sessionKey.toString(), sessionModelJson);
		
		// Ensure that it really is in the database
		String sessionModelJsonFromDB = (String) this.couchbaseClient.get(sessionKey.toString());
		assertNotNull(sessionModelJsonFromDB);
		
		
		CouchbaseSessionStore couchbaseSessionStore = new CouchbaseSessionStore();
		couchbaseSessionStore.setCouchbaseClient(this.couchbaseClient);
		couchbaseSessionStore.setCryptoService(cryptoService);
		couchbaseSessionStore.setApplicationId("WonderApp");

		
		/*
		 * Method Under Test
		 */
		try {
			couchbaseSessionStore.remove("1234abcd");
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
		
		/*
		 * Ensure that the results are NOT in database
		 */
		String resultSessionModelJson = (String) this.couchbaseClient.get(sessionKey.toString());
		assertNull(resultSessionModelJson);
		
	} // END testRemove Method
	
	
//	@Test
//	public void testLoad() {
//	
//		/*
//		 * Fixtures
//		 */
//		SessionKey sessionKey = new SessionKey("WonderApp", "1234abcd");
//		CryptoService cryptoService = new PKCS8CryptoService("src/resources/privkey.pkcs8.pem");
//		
//		SessionModel sessionModel = new SessionModel();
//		sessionModel.setDataEncoding(SessionModel.BASE64);
//		sessionModel.setPrivateKeyId("08_17_2014");
//		Date createdDate = new Date();
//		sessionModel.setCreateTime(createdDate);
//		Date updatedDate = new Date();
//		sessionModel.setUpdateTime(updatedDate);
//		String data = "This is the data!";
//		byte[] dataBytes = cryptoService.encrypt(data);
//		byte[] dataBase64 = Base64.getEncoder().encode(dataBytes);
//		sessionModel.setData(new String(dataBase64));
//		
//		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateJsonSerializer()).create();
//		String sessionModelJson = gson.toJson(sessionModel);
//		
//		// Put SessionJSON into the database
//		this.couchbaseClient.set(sessionKey.toString(), sessionModelJson);
//		
//		// Ensure that it really is in the database
//		String sessionModelJsonFromDB = (String) this.couchbaseClient.get(sessionKey.toString());
//		assertNotNull(sessionModelJsonFromDB);
//		
//		
//		CouchbaseSessionStore couchbaseSessionStore = new CouchbaseSessionStore();
//		couchbaseSessionStore.setCouchbaseClient(this.couchbaseClient);
//		couchbaseSessionStore.setCryptoService(cryptoService);
//		couchbaseSessionStore.setApplicationId("WonderApp");
//		
//		Manager mockedManager = mock(CouchbaseSecureSessionManager.class);
//		Session mockedSession = mock(Session.class);
//		HttpSession mockedHttpSession = mock(HttpSession.class);
//		when(mockedSession.getSession()).thenReturn(mockedHttpSession);
//		
//		
//		
//		
//		couchbaseSessionStore.setManager(manager);
//
//		
//		/*
//		 * Method Under Test
//		 */
//		Session session = null;
//		try {
//			
//			session = couchbaseSessionStore.load("1234abcd");
//			
//		} catch (IOException | ClassNotFoundException ioe) {
//			ioe.printStackTrace();
//		}
//		
//
//		assertNotNull(session);
//		
//		
//	} // END testRemove Method
	
	
} // END CouchbaseSessionStoreTest Class
