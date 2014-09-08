package co.ssessions.web;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Pipeline;
import org.apache.catalina.startup.Tomcat;

import co.ssessions.couchbase.CouchbaseSecureSessionsManager;
import co.ssessions.valve.SecureSessionsValve;
import co.ssessions.web.servlet.SessionTestServlet;

public class TomcatServerApplication {
	
	
	
	
	public static void main(String[] args) {
		
		// TODO Add some distinct logging here
		// TODO Do defensive programming
		// TODO Add more options here
		
		System.out.println("Starting TomcatApplication");
		
		int portNumber = Integer.parseInt(args[0]);
		
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(portNumber);

		File base = new File("src/test/resources/embedded_tomcat");

		Context rootCtx = tomcat.addContext("/", base.getAbsolutePath());
		
		Pipeline pipe = rootCtx.getPipeline();
		pipe.addValve(new SecureSessionsValve());
		
		CouchbaseSecureSessionsManager secureSessionManagger = new CouchbaseSecureSessionsManager();
		
		secureSessionManagger.setConfigFilePath("couchbase_manager_conf.properties");

		rootCtx.setManager(secureSessionManagger);

		Tomcat.addServlet(rootCtx, "SessionTestServlet", new SessionTestServlet());

		rootCtx.addServletMapping("/a", "SessionTestServlet");
		
		try {
			tomcat.start();
			tomcat.getServer().await();
		} catch (LifecycleException e) {
			// TODO Handle this gracefully
			e.printStackTrace();
		}
		
	}
	
}
