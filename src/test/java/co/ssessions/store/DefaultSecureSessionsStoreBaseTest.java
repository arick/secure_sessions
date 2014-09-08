package co.ssessions.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
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
		
		builder.setPortNumber(8888);
		builder.setBaseFolder("src/test/resources/embedded_tomcat");
		builder.setSecureSessionManagger(new CouchbaseSecureSessionsManager());
		builder.setSecureSessionsValve(new SecureSessionsValve());
		builder.setHttpServlet(new SessionTestServlet());
		
		Map<String, String> serverConfig1 = builder.startNewTomcatApplication();
		this.loadBalancer.addServerConfig(serverConfig1);
		System.out.println("URL 1: " + serverConfig1.get("URL"));
		
		
		
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
		
		
		
//		System.out.println("I'm sleepy....");
//		try {
//			Thread.sleep(10000000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("I'm awake....");
		

	} // END tomcatSetup Method

	
	public void tearDown() {
		
		this.couchbaseClient = null;
		this.cryptoService = null;
		this.secureSessionsStoreBase = null;
		
		EnvConfig.clearSingleton();
		
	} // END tearDown Method

	

	@Test
	public void testLoad() {

		this.tomcatSetup();

		System.out.println("Let's sleep for 5 seconds while the tomcat servers boot up....");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(".... Yawn!!! Let's start sending queries!");
		
		
		/*
		 * Start the HTTP Queries
		 */
		
		HttpClientContext httpClientContext = new HttpClientContext();
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		
		String targetURL = this.loadBalancer.getNextServerUrl();
		System.out.println("Target URL 1: " + targetURL);
		HttpGet httpget = new HttpGet(targetURL + "?h=2&q=3");
		
		
		
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget, httpClientContext);
			
			
			Reader contentReader = new InputStreamReader(response.getEntity().getContent());
			BufferedReader contentBufferedReader = new BufferedReader(contentReader);
			
			System.out.println("\n\nWeb Page Content (" + targetURL + "):");
			String errorLine = contentBufferedReader.readLine();
			while (errorLine != null) {
				
				System.out.println(errorLine);
				errorLine = contentBufferedReader.readLine();
			}	
			
			System.out.println("\n\nCookie Contents (" + targetURL + "):");
			List<Cookie> cookies = httpClientContext.getCookieStore().getCookies();
			for (Cookie cookie : cookies) {
				System.out.println("Cookie 1: " + cookie.getDomain() + " / " + cookie.getName() + " / " + cookie.getValue());
			}
			
			response.close();
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
		
		
		
		
		
		
	} // END testRemove Method

	
	
} // END DefaultSecureSessionsStoreBaseTest Class
