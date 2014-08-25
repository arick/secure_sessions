package co.ssessions.couchbase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;
import net.spy.memcached.PersistTo;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StandardSessionFacade;
import org.apache.catalina.startup.Tomcat;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import co.ssessions.SessionKey;
import co.ssessions.SessionModel;
import co.ssessions.conf.EnvConfig;
import co.ssessions.crypto.CryptoService;
import co.ssessions.crypto.PKCS8CryptoService;
import co.ssessions.json.DateJsonDeserializer;
import co.ssessions.json.DateJsonSerializer;
import co.ssessions.testSupport.embeddedTomcat.DatePrintServlet;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CouchbaseSessionStoreTest extends TestCase {

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
			CouchbaseConnectionFactory cf = new CouchbaseConnectionFactory(hosts, bucket, password);
			this.couchbaseClient = new CouchbaseClient(cf);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	} // END setUp Method

	
	private void tomcatSetup() {

		try {
			Tomcat tomcat = new Tomcat();
			tomcat.setPort(8080);

			File base = new File("src/test/resources/embedded_tomcat");

			Context rootCtx = tomcat.addContext("/app", base.getAbsolutePath());

			CouchbaseSecureSessionManager couchbaseSecureSessionManager = new CouchbaseSecureSessionManager();
			couchbaseSecureSessionManager
					.setPrivateKeyFilePath("src/test/resources/crypto/privkey.pkcs8.pem");
			couchbaseSecureSessionManager.setBucket("default");
			couchbaseSecureSessionManager
					.setHosts("http://localhost:8091/pools");
			couchbaseSecureSessionManager
					.setApplicationId("EmbeddedTesterApplication");

			rootCtx.setManager(couchbaseSecureSessionManager);

			Tomcat.addServlet(rootCtx, "dateServlet", new DatePrintServlet());

			rootCtx.addServletMapping("/date", "dateServlet");

			tomcat.start();
			tomcat.getServer().await();

		} catch (LifecycleException le) {
			le.printStackTrace();
		}

	} // END tomcatSetup Method

	public void tearDown() {

		this.couchbaseClient.shutdown();
		this.couchbaseClient = null;

	} // END tearDown Method

	@Test
	public void testLoad() {

		this.tomcatSetup();

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
		// CouchbaseSessionStore couchbaseSessionStore = new
		// CouchbaseSessionStore();
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
		// Manager mockedManager = mock(CouchbaseSecureSessionManager.class);
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
	// CouchbaseSessionStore couchbaseSessionStore = new
	// CouchbaseSessionStore();
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
	// CouchbaseSessionStore couchbaseSessionStore = new
	// CouchbaseSessionStore();
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
	// CouchbaseSessionStore couchbaseSessionStore = new
	// CouchbaseSessionStore();
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
	// Manager mockedManager = mock(CouchbaseSecureSessionManager.class);
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

} // END CouchbaseSessionStoreTest Class
