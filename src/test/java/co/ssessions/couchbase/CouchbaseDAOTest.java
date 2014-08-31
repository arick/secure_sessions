package co.ssessions.couchbase;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import net.spy.memcached.PersistTo;

import org.junit.Test;

import co.ssessions.conf.EnvConfig;
import co.ssessions.dao.SecureSessionsDAO;
import co.ssessions.json.DateJsonDeserializer;
import co.ssessions.json.DateJsonSerializer;
import co.ssessions.models.SessionKey;
import co.ssessions.models.SessionModel;

import com.couchbase.client.CouchbaseClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;


public class CouchbaseDAOTest extends TestCase {


	CouchbaseClient couchbaseClient = null;
	Injector injector = null;
	SecureSessionsDAO secureSessionsDAO = null;
	
	public void setUp() {
		
		String managerConfigFilePath = EnvConfig.getSafe("tomcat_manager_conf.propertiesFilePath");
		EnvConfig.load(managerConfigFilePath);
		
		this.injector = Guice.createInjector(new CouchbaseSecureSessionsModule());
		CouchbaseClientHolder cch = this.injector.getInstance(CouchbaseClientHolder.class);
		
		assertNotNull(cch);
		
		CouchbaseClient client = cch.getInstance();
		
		assertNotNull(client);
		assertTrue(client instanceof CouchbaseClient);
		
		this.couchbaseClient = client;
		
		
		this.secureSessionsDAO = this.injector.getInstance(SecureSessionsDAO.class);
		
	} // END setUp Method
	
	
	
	public void tearDown() {
		this.couchbaseClient = null;
		this.injector = null;
		this.secureSessionsDAO = null;
		EnvConfig.clearSingleton();
	}
	

	/* ********************************************************************************
	 * Store Session Tests
	 * ********************************************************************************/

	@Test
	public void testStoreSession_Valid() {
	
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
		this.secureSessionsDAO.storeSession(sessionKey, sessionModel);
		
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
		this.couchbaseClient.delete(sessionKey.toString(), PersistTo.ONE);
	}
	
	
	@Test
	public void testStoreSession_SessionKey_Is_Null() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = null;
		
		SessionModel sessionModel = new SessionModel();
		sessionModel.setDataEncoding(SessionModel.BASE64);
		sessionModel.setPrivateKeyId("08_17_2014");
		Date createdDate = new Date();
		sessionModel.setCreateTime(createdDate);
		Date updatedDate = new Date();
		sessionModel.setUpdateTime(updatedDate);
		String data = "This is the data!";
		sessionModel.setData(data);
		
		
		boolean exceptionCaught = false;
		/*
		 * Method Under Test
		 */
		try {
			this.secureSessionsDAO.storeSession(sessionKey, sessionModel);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
		
	}
	
	
	@Test
	public void testStoreSession_SessionModel_Is_Null() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = new SessionKey("WonderApp", "1234abcd");
		SessionModel sessionModel = null;
		
		boolean exceptionCaught = false;
		/*
		 * Method Under Test
		 */
		try {
			this.secureSessionsDAO.storeSession(sessionKey, sessionModel);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
		
	}
	
	
	@Test
	public void testStoreSession_All_Parameters_are_Null() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = null;
		SessionModel sessionModel = null;
		
		boolean exceptionCaught = false;
		/*
		 * Method Under Test
		 */
		try {
			this.secureSessionsDAO.storeSession(sessionKey, sessionModel);
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
		
	}
	
	
	/* ********************************************************************************
	 * Delete Session Tests
	 * ********************************************************************************/
	
	@Test
	public void testDeleteSession_Valid() {
	
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
		this.secureSessionsDAO.deleteSession(sessionKey);
		
		/*
		 * Ensure that the results are NOT in database
		 */
		String resultSessionModelJson = (String) this.couchbaseClient.get(sessionKey.toString());
		assertNull(resultSessionModelJson);
		
	}
	
	
	
	@Test
	public void testDeleteSession_SessionKey_Is_Null() {
	
		/*
		 * Fixtures
		 */
		SessionKey sessionKey = null;
		
		
		boolean exceptionCaught = false;
		/*
		 * Method Under Test
		 */
		try {
			
			this.secureSessionsDAO.deleteSession(sessionKey);
		
		} catch(IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
		
	}
	
	
	/* ********************************************************************************
	 * Delete Sessions Tests
	 * ********************************************************************************/
	
	@Test
	public void testDeleteSessions_Valid() {
	
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
		this.secureSessionsDAO.deleteSessions(sessionKeys);
		
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
	public void testDeleteSessions_SessionKeys_Is_Null() {
	
		/*
		 * Fixtures
		 */
		Set<SessionKey> sessionKeys = null;
		
		
		boolean exceptionCaught = false;
		/*
		 * Method Under Test
		 */
		try {
			
			this.secureSessionsDAO.deleteSessions(sessionKeys);
			
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
		
	}
	
	
	@Test
	public void testDeleteSessions_SessionKeys_Is_Empty_Set() {
	
		/*
		 * Fixtures
		 */
		Set<SessionKey> sessionKeys = new HashSet<SessionKey>();
		
		
		boolean exceptionCaught = false;
		/*
		 * Method Under Test
		 */
		try {
			
			this.secureSessionsDAO.deleteSessions(sessionKeys);
			
		} catch (IllegalArgumentException iae) {
			exceptionCaught = true;
		}
		
		assertTrue(exceptionCaught);
		
	}
	
	/* ********************************************************************************
	 * Retreive Session Tests
	 * ********************************************************************************/
	
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
		SessionModel resultSessionModel = this.secureSessionsDAO.retrieveSession(sessionKey);
		
		
		assertEquals(updatedDate, resultSessionModel.getUpdateTime());
		assertEquals(createdDate, resultSessionModel.getCreateTime());
		assertEquals(data, resultSessionModel.getData());
		assertEquals(SessionModel.BASE64, resultSessionModel.getDataEncoding());
		assertEquals("08_17_2014", resultSessionModel.getPrivateKeyId());
		
		// Remove data from database
		this.couchbaseClient.delete(sessionKey.toString(), PersistTo.ONE);
		
	}
	
	
	
	/* ********************************************************************************
	 * Number of Session Tests
	 * ********************************************************************************/
	
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
		long count = this.secureSessionsDAO.numberOfSessions(sessionKey1);
		
		assertEquals(3, count);
		
		this.couchbaseClient.delete(sessionKey1.toString(), PersistTo.ONE);
		this.couchbaseClient.delete(sessionKey2.toString(), PersistTo.ONE);
		this.couchbaseClient.delete(sessionKey3.toString(), PersistTo.ONE);
		this.couchbaseClient.delete(sessionKey4.toString(), PersistTo.ONE);
		
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
		long count = this.secureSessionsDAO.numberOfSessions(sessionKey3);
		
		assertEquals(1, count);
		
		this.couchbaseClient.delete(sessionKey1.toString(), PersistTo.ONE);
		this.couchbaseClient.delete(sessionKey2.toString(), PersistTo.ONE);
		this.couchbaseClient.delete(sessionKey3.toString(), PersistTo.ONE);
		this.couchbaseClient.delete(sessionKey4.toString(), PersistTo.ONE);
		
	} // END testNumberOfSession_2 Method

} // END CouchbaseDAOTest Class
