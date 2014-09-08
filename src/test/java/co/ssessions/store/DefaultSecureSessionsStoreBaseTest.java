package co.ssessions.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import co.ssessions.conf.EnvConfig;
import co.ssessions.couchbase.CouchbaseClientHolder;
import co.ssessions.couchbase.CouchbaseSecureSessionsManager;
import co.ssessions.couchbase.CouchbaseSecureSessionsModule;
import co.ssessions.crypto.CryptoService;
import co.ssessions.valve.SecureSessionsValve;
import co.ssessions.web.LoadBalancer;
import co.ssessions.web.TomcatServer;
import co.ssessions.web.TomcatServerBuilder;
import co.ssessions.web.servlet.SessionTestServlet;

import com.couchbase.client.CouchbaseClient;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DefaultSecureSessionsStoreBaseTest extends TestCase {

	CouchbaseClient couchbaseClient = null;
	CryptoService cryptoService = null;
	Injector injector = null;
	SecureSessionsStoreBase secureSessionsStoreBase = null;
	LoadBalancer loadBalancer;

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
		
		this.secureSessionsStoreBase = this.injector.getInstance(SecureSessionsStoreBase.class);
		
	} // END setUp Method

	
	private void tomcatSetup() {

		System.out.println("Starting Tomcat Setup");
		
		this.loadBalancer = new LoadBalancer();
		
		TomcatServerBuilder builder = new TomcatServerBuilder();
		
		builder.setPortNumber(7070);
		builder.setBaseFolder("src/test/resources/embedded_tomcat");
		builder.setSecureSessionManagger(new CouchbaseSecureSessionsManager());
		builder.setSecureSessionsValve(new SecureSessionsValve());
		builder.setHttpServlet(new SessionTestServlet());
		
		Map<String, String> serverConfig1 = builder.startNewTomcatApplication();
		this.loadBalancer.addServerConfig(serverConfig1);
		System.out.println("URL 1: " + serverConfig1.get("URL"));
//		
//		
//		
//		builder.setPortNumber(8080);
//		builder.setBaseFolder("src/test/resources/embedded_tomcat");
//		builder.setSecureSessionManagger(new CouchbaseSecureSessionsManager());
//		builder.setSecureSessionsValve(new SecureSessionsValve());
//		builder.setHttpServlet(new SessionTestServlet());
//		
//		Map<String, String> serverConfig2 = builder.startNewTomcatApplication();
//		this.loadBalancer.addServerConfig(serverConfig2);
//		System.out.println("URL 2: " + serverConfig2.get("URL"));
//		
//		
//		
//		builder.setPortNumber(9090);
//		builder.setSecureSessionManagger(new CouchbaseSecureSessionsManager());
//		builder.setSecureSessionsValve(new SecureSessionsValve());
//		builder.setHttpServlet(new SessionTestServlet());
//		
//		Map<String, String> serverConfig3 = builder.startNewTomcatApplication();
//		this.loadBalancer.addServerConfig(serverConfig3);
//		System.out.println("URL 3: " + serverConfig3.get("URL"));
		
		
		
		/*
		 * Start the HTTP Queries
		 */
		
		HttpClientContext httpClientContext = new HttpClientContext();
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
	
		HttpGet httpget = new HttpGet("http://localhost:7070/a");
		
		
		
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget, httpClientContext);
			
			
			Reader errorReader = new InputStreamReader(response.getEntity().getContent());
			BufferedReader errorBufferedReader = new BufferedReader(errorReader);
			
			String errorLine = errorBufferedReader.readLine();
			while (errorLine != null) {
				
				System.out.println(errorLine);
				errorLine = errorBufferedReader.readLine();
			}	
			
			
			
			response.close();
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
		
		
		
		
		
		
		
		
		
		System.out.println("I'm sleepy....");
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("I'm awake....");
		
//		try {
//			Tomcat tomcat = new Tomcat();
//			tomcat.setPort(8080);
//
//			File base = new File(EnvConfig.getSafe("embedded_tomcat.baseFolder"));
//
//			Context rootCtx = tomcat.addContext("/app", base.getAbsolutePath());
//			
//			Pipeline pipe = rootCtx.getPipeline();
//			TestValve valve = new TestValve();
//			pipe.addValve(valve);
//			SecureSessionsValve ssValve = new SecureSessionsValve();
//			pipe.addValve(ssValve);
//			Test2Valve test2Valve = new Test2Valve();
//			pipe.addValve(test2Valve);
//			
//			
//			CouchbaseSecureSessionsManager couchbaseSecureSessionManager = new CouchbaseSecureSessionsManager();
//
//			couchbaseSecureSessionManager.setConfigFilePath(EnvConfig.getSafe("tomcat_manager_conf.propertiesFilePath"));
//
//			rootCtx.setManager(couchbaseSecureSessionManager);
//			
//
//			Tomcat.addServlet(rootCtx, "dateServlet", new DatePrintServlet());
//
//			rootCtx.addServletMapping("/date", "dateServlet");
//
//			tomcat.start();
//			tomcat.getServer().await();
//
//		} catch (LifecycleException le) {
//			le.printStackTrace();
//		}

	} // END tomcatSetup Method

	
	public void tearDown() {
		
		this.couchbaseClient = null;
		this.cryptoService = null;
		this.secureSessionsStoreBase = null;
		
		EnvConfig.clearSingleton();
		
	} // END tearDown Method

	

	@Test
	public void testLoad() {

//		this.tomcatSetup();

		 /*
		 * Fixtures
		 */
//		 String sessionId = "1234abcd";
//		 String applicationId = "WonderApp";
//		 SessionKey sessionKey = new SessionKey(applicationId, sessionId);
//		 
//		
//		 SessionModel sessionModel = new SessionModel();
//		 sessionModel.setDataEncoding(SessionModel.BASE64);
//		 sessionModel.setPrivateKeyId("08_17_2014");
//		 Date createdDate = new Date();
//		 sessionModel.setCreateTime(createdDate);
//		 Date updatedDate = new Date();
//		 sessionModel.setUpdateTime(updatedDate);
//		 String data = "This is the data!";
//		 byte[] dataBytes = this.cryptoService.encrypt(data);
//		 byte[] dataBase64 = Base64.getEncoder().encode(dataBytes);
//		 sessionModel.setData(new String(dataBase64));
//		
//		 Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new
//		 DateJsonSerializer()).create();
//		 String sessionModelJson = gson.toJson(sessionModel);
//		
//		 // Put SessionJSON into the database
//		 this.couchbaseClient.set(sessionKey.toString(), sessionModelJson, PersistTo.ONE);
//		
//		 // Ensure that it really is in the database
//		 String sessionModelJsonFromDB = (String)
//		 this.couchbaseClient.get(sessionKey.toString());
//		 assertNotNull(sessionModelJsonFromDB);
		
		
//		 DefaultSecureSessionsStoreBase couchbaseSessionStore = new
//		 DefaultSecureSessionsStoreBase();
//		 couchbaseSessionStore.setCouchbaseClient(this.couchbaseClient);
//		 couchbaseSessionStore.setCryptoService(cryptoService);
//		 couchbaseSessionStore.setApplicationId("WonderApp");
//		
//		 Loader mockedLoader = mock(Loader.class);
//		 when(mockedLoader.getClassLoader()).thenReturn(couchbaseSessionStore.getClass().getClassLoader());
//		
//		 Container mockedContainer = mock(Container.class);
//		 when(mockedContainer.getLoader()).thenReturn(mockedLoader);
//		
//		 Manager mockedManager = mock(CouchbaseSecureSessionsManager.class);
//		 when(mockedManager.getContainer()).thenReturn(mockedContainer);
//		
//		 ServletContext servletContext = new MockServletContext();
//		 HttpSession httpSession = new MockHttpSession(servletContext);
//		
//		 // Session mockedSession = mock(Session.class);
//		 StandardSession standardSession = new StandardSession(mockedManager);
//		 // Session session = new StandardSessionFacade(httpSession);
//		
//		 when(mockedManager.createSession(sessionId)).thenReturn(standardSession);
//		
//		 couchbaseSessionStore.setManager(mockedManager);
//		
//		
//		 /*
//		 * Method Under Test
//		 */
//		 Session resultSession = null;
//		 try {
//		
//		 resultSession = this.secureSessionsStoreBase.load(sessionId);
//		
//		 } catch (IOException | ClassNotFoundException ioe) {
//		 ioe.printStackTrace();
//		 }
//		
//		
//		 assertNotNull(resultSession);

	} // END testRemove Method

	// @Test
	// public void testSave_1() {
	//
	// /*
	// * Fixtures
	// */
	// HttpSession mockedHttpSession = mock(HttpSession.class);
	// String fakeSessionId = "The_Session_Id";
	// when(mockedHttpSession.getId()).thenReturn(fakeSessionId);
	// when(mockedHttpSession.getAttribute("first_name")).thenReturn("Jasmine");
	// when(mockedHttpSession.getAttribute("last_name")).thenReturn("Moore");
	// when(mockedHttpSession.getAttribute("age")).thenReturn("4");
	//
	// Vector<String> attributeNames = new Vector<String>();
	// attributeNames.addElement("first_name");
	// attributeNames.addElement("last_name");
	// attributeNames.addElement("age");
	// when(mockedHttpSession.getAttributeNames()).thenReturn(attributeNames.elements());
	//
	//
	// Session mockedSession = mock(Session.class);
	// when(mockedSession.getSession()).thenReturn(mockedHttpSession);
	//
	//
	// DefaultSecureSessionsStoreBase couchbaseSessionStore = new
	// DefaultSecureSessionsStoreBase();
	// couchbaseSessionStore.setCouchbaseClient(this.couchbaseClient);
	// CryptoService cryptoService = new
	// PKCS8CryptoService("src/test/resources/crypto/privkey.pkcs8.pem");
	// couchbaseSessionStore.setCryptoService(cryptoService);
	// couchbaseSessionStore.setApplicationId("WonderApp");
	//
	//
	// /*
	// * Method Under Test
	// */
	// try {
	// couchbaseSessionStore.save(mockedSession);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	//
	// // Get data from database
	// SessionKey resultSessionKey = new
	// SessionKey(couchbaseSessionStore.getApplicationId(),
	// mockedHttpSession.getId());
	// String resultSessionModelJson = (String)
	// this.couchbaseClient.get(resultSessionKey.toString());
	// Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new
	// DateJsonDeserializer()).create();
	// SessionModel sessionModel = (SessionModel)
	// gson.fromJson(resultSessionModelJson, SessionModel.class);
	//
	// assertNotNull(sessionModel);
	// assertNotNull(sessionModel.getData()); // Just check that the
	// sessionModel data element is present - it's encrypted
	//
	//
	// // Clean up data from database
	// SessionKey cleanUpSessionKey = new
	// SessionKey(couchbaseSessionStore.getApplicationId(),
	// mockedHttpSession.getId());
	// this.couchbaseClient.delete(cleanUpSessionKey.toString());
	//
	//
	// } // END testSave Method

	// TODO: Test the updateDate changes properly... save a session, load a
	// session and check for divergent create and update dates. Also check for
	// consistency in data between session stores

	// TODO: Test for error conditions... ie no session data for some reason
	// TODO: Test for saving the session only when a session actually changes.
	// maybe.

	// @Test
	// public void testRemove() {
	//
	// /*
	// * Fixtures
	// */
	// SessionKey sessionKey = new SessionKey("WonderApp", "1234abcd");
	// CryptoService cryptoService = new
	// PKCS8CryptoService("src/test/resources/crypto/privkey.pkcs8.pem");
	//
	// SessionModel sessionModel = new SessionModel();
	// sessionModel.setDataEncoding(SessionModel.BASE64);
	// sessionModel.setPrivateKeyId("08_17_2014");
	// Date createdDate = new Date();
	// sessionModel.setCreateTime(createdDate);
	// Date updatedDate = new Date();
	// sessionModel.setUpdateTime(updatedDate);
	// String data = "This is the data!";
	// byte[] dataBytes = cryptoService.encrypt(data);
	// byte[] dataBase64 = Base64.getEncoder().encode(dataBytes);
	// sessionModel.setData(new String(dataBase64));
	//
	// Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new
	// DateJsonSerializer()).create();
	// String sessionModelJson = gson.toJson(sessionModel);
	//
	// // Put SessionJSON into the database
	// this.couchbaseClient.set(sessionKey.toString(), sessionModelJson,
	// PersistTo.ONE);
	//
	// // Ensure that it really is in the database
	// String sessionModelJsonFromDB = (String)
	// this.couchbaseClient.get(sessionKey.toString());
	// assertNotNull(sessionModelJsonFromDB);
	//
	//
	// DefaultSecureSessionsStoreBase couchbaseSessionStore = new
	// DefaultSecureSessionsStoreBase();
	// couchbaseSessionStore.setCouchbaseClient(this.couchbaseClient);
	// couchbaseSessionStore.setCryptoService(cryptoService);
	// couchbaseSessionStore.setApplicationId("WonderApp");
	//
	//
	// /*
	// * Method Under Test
	// */
	// try {
	// couchbaseSessionStore.remove("1234abcd");
	// } catch (IOException ioe) {
	// // TODO Auto-generated catch block
	// ioe.printStackTrace();
	// }
	//
	// /*
	// * Ensure that the results are NOT in database
	// */
	// String resultSessionModelJson = (String)
	// this.couchbaseClient.get(sessionKey.toString());
	// assertNull(resultSessionModelJson);
	//
	// } // END testRemove Method

	//
	// @Test
	// public void testLoad() {
	//
	// /*
	// * Fixtures
	// */
	// String sessionId = "1234abcd";
	// String applicationId = "WonderApp";
	// SessionKey sessionKey = new SessionKey(applicationId, sessionId);
	// CryptoService cryptoService = new
	// PKCS8CryptoService("src/test/resources/crypto/privkey.pkcs8.pem");
	//
	// SessionModel sessionModel = new SessionModel();
	// sessionModel.setDataEncoding(SessionModel.BASE64);
	// sessionModel.setPrivateKeyId("08_17_2014");
	// Date createdDate = new Date();
	// sessionModel.setCreateTime(createdDate);
	// Date updatedDate = new Date();
	// sessionModel.setUpdateTime(updatedDate);
	// String data = "This is the data!";
	// byte[] dataBytes = cryptoService.encrypt(data);
	// byte[] dataBase64 = Base64.getEncoder().encode(dataBytes);
	// sessionModel.setData(new String(dataBase64));
	//
	// Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new
	// DateJsonSerializer()).create();
	// String sessionModelJson = gson.toJson(sessionModel);
	//
	// // Put SessionJSON into the database
	// this.couchbaseClient.set(sessionKey.toString(), sessionModelJson,
	// PersistTo.ONE);
	//
	// // Ensure that it really is in the database
	// String sessionModelJsonFromDB = (String)
	// this.couchbaseClient.get(sessionKey.toString());
	// assertNotNull(sessionModelJsonFromDB);
	//
	//
	// DefaultSecureSessionsStoreBase couchbaseSessionStore = new
	// DefaultSecureSessionsStoreBase();
	// couchbaseSessionStore.setCouchbaseClient(this.couchbaseClient);
	// couchbaseSessionStore.setCryptoService(cryptoService);
	// couchbaseSessionStore.setApplicationId("WonderApp");
	//
	// Loader mockedLoader = mock(Loader.class);
	// when(mockedLoader.getClassLoader()).thenReturn(couchbaseSessionStore.getClass().getClassLoader());
	//
	// Container mockedContainer = mock(Container.class);
	// when(mockedContainer.getLoader()).thenReturn(mockedLoader);
	//
	// Manager mockedManager = mock(CouchbaseSecureSessionsManager.class);
	// when(mockedManager.getContainer()).thenReturn(mockedContainer);
	//
	// ServletContext servletContext = new MockServletContext();
	// HttpSession httpSession = new MockHttpSession(servletContext);
	//
	// // Session mockedSession = mock(Session.class);
	// StandardSession standardSession = new StandardSession(mockedManager);
	// // Session session = new StandardSessionFacade(httpSession);
	//
	// when(mockedManager.createSession(sessionId)).thenReturn(standardSession);
	//
	// couchbaseSessionStore.setManager(mockedManager);
	//
	//
	// /*
	// * Method Under Test
	// */
	// Session resultSession = null;
	// try {
	//
	// resultSession = couchbaseSessionStore.load(sessionId);
	//
	// } catch (IOException | ClassNotFoundException ioe) {
	// ioe.printStackTrace();
	// }
	//
	//
	// assertNotNull(resultSession);
	//
	//
	// } // END testRemove Method

} // END DefaultSecureSessionsStoreBaseTest Class
